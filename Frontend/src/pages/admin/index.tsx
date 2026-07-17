import React, { useEffect, useState } from 'react'
import {
  Users, Stethoscope, Calendar, TrendingDown,
  Plus, Trash2, Edit2, CheckCircle, XCircle,
} from 'lucide-react'
import { dashboardService, appointmentService, specialtyService } from '@/services'
import type { Appointment, DashboardSummary, Specialty } from '@/types'
import { Button, StatusBadge, EmptyState, Spinner, Input, Modal } from '@/components/common'
import { formatDate } from '@/utils'
import toast from 'react-hot-toast'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'

// ─── Admin Dashboard ──────────────────────────────────────────────────────────
export const AdminDashboard: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    dashboardService.getSummary()
      .then((r) => setSummary(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="flex justify-center py-16"><Spinner size={36} /></div>
  if (!summary) return null

  const statCards = [
    { label: 'Tổng bệnh nhân',  value: summary.totalPatients,     icon: Users,       color: 'bg-blue-50   text-blue-600'  },
    { label: 'Tổng bác sĩ',    value: summary.totalDoctors,       icon: Stethoscope, color: 'bg-green-50  text-green-600' },
    { label: 'Tổng lịch hẹn',  value: summary.totalAppointments,  icon: Calendar,    color: 'bg-purple-50 text-purple-600'},
    { label: 'Tỷ lệ hủy',      value: `${summary.cancelRate}%`,   icon: TrendingDown,color: 'bg-red-50    text-red-600'   },
  ]

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Tổng quan hệ thống</h1>

      {/* Stat cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="card p-5">
            <div className={`w-11 h-11 rounded-xl flex items-center justify-center mb-3 ${color}`}>
              <Icon size={20} />
            </div>
            <p className="text-3xl font-bold text-gray-900">{value}</p>
            <p className="text-sm text-gray-500 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Appointment status breakdown */}
      <div className="card p-5">
        <h2 className="font-semibold text-gray-900 mb-4">Phân bổ trạng thái lịch hẹn</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {[
            { label: 'Chờ xác nhận', value: summary.pendingAppointments,   cls: 'badge-pending' },
            { label: 'Đã xác nhận',  value: summary.confirmedAppointments, cls: 'badge-confirmed' },
            { label: 'Hoàn thành',   value: summary.completedAppointments, cls: 'badge-completed' },
            { label: 'Đã hủy',       value: summary.cancelledAppointments, cls: 'badge-cancelled' },
          ].map(({ label, value, cls }) => (
            <div key={label} className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
              <span className={cls}>{label}</span>
              <span className="font-bold text-gray-900">{value}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Monthly stats table */}
      {summary.monthlyStats.length > 0 && (
        <div className="card p-5">
          <h2 className="font-semibold text-gray-900 mb-4">Thống kê theo tháng</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100">
                  {['Tháng', 'Tổng', 'Hoàn thành', 'Đã hủy'].map((h) => (
                    <th key={h} className="text-left py-2 px-3 font-medium text-gray-500">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {summary.monthlyStats.map((m) => (
                  <tr key={m.month} className="border-b border-gray-50 hover:bg-gray-50">
                    <td className="py-2 px-3 font-medium text-gray-700">{m.month}</td>
                    <td className="py-2 px-3 text-gray-900">{m.total}</td>
                    <td className="py-2 px-3 text-green-600">{m.completed}</td>
                    <td className="py-2 px-3 text-red-500">{m.cancelled}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── Admin Appointments ───────────────────────────────────────────────────────
export const AdminAppointmentsPage: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')

  useEffect(() => {
    appointmentService.getAll()
      .then((r) => setAppointments(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const filtered = appointments.filter((a) => {
    const matchSearch =
      a.patient.fullName.toLowerCase().includes(search.toLowerCase()) ||
      a.doctor.fullName.toLowerCase().includes(search.toLowerCase())
    const matchStatus = !statusFilter || a.status === statusFilter
    return matchSearch && matchStatus
  })

  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      await appointmentService.updateStatus(id, status)
      toast.success('Cập nhật thành công')
      setAppointments((prev) =>
        prev.map((a) => a.appointmentId === id ? { ...a, status: status as any } : a)
      )
    } catch {}
  }

  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Quản lý lịch hẹn</h1>

      {/* Filters */}
      <div className="flex gap-3 flex-wrap">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm theo tên bệnh nhân, bác sĩ..."
          className="input flex-1 min-w-[200px]"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="input w-44"
        >
          <option value="">Tất cả trạng thái</option>
          {['PENDING','CONFIRMED','CHECKED_IN','COMPLETED','CANCELLED','NO_SHOW'].map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr>
                  {['ID', 'Bệnh nhân', 'Bác sĩ', 'Ngày khám', 'Giờ', 'Trạng thái', 'Thao tác'].map((h) => (
                    <th key={h} className="text-left py-3 px-4 font-medium text-gray-500 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.length === 0 ? (
                  <tr><td colSpan={7} className="text-center py-12 text-gray-400">Không có dữ liệu</td></tr>
                ) : filtered.map((a) => (
                  <tr key={a.appointmentId} className="border-b border-gray-50 hover:bg-gray-50">
                    <td className="py-3 px-4 text-gray-400">#{a.appointmentId}</td>
                    <td className="py-3 px-4 font-medium text-gray-900 whitespace-nowrap">{a.patient.fullName}</td>
                    <td className="py-3 px-4 text-gray-600 whitespace-nowrap">BS. {a.doctor.fullName}</td>
                    <td className="py-3 px-4 text-gray-600 whitespace-nowrap">{formatDate(a.appointmentDate)}</td>
                    <td className="py-3 px-4 text-gray-600">{a.timeSlot}</td>
                    <td className="py-3 px-4"><StatusBadge status={a.status} /></td>
                    <td className="py-3 px-4">
                      <div className="flex gap-2">
                        {a.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => handleUpdateStatus(a.appointmentId, 'CONFIRMED')}
                              className="p-1.5 rounded text-green-600 hover:bg-green-50"
                              title="Xác nhận"
                            >
                              <CheckCircle size={16} />
                            </button>
                            <button
                              onClick={() => handleUpdateStatus(a.appointmentId, 'CANCELLED')}
                              className="p-1.5 rounded text-red-500 hover:bg-red-50"
                              title="Hủy"
                            >
                              <XCircle size={16} />
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── Admin Specialties ────────────────────────────────────────────────────────
const specialtySchema = z.object({
  name:        z.string().min(1, 'Không được để trống').max(100),
  description: z.string().max(500).optional(),
  iconUrl:     z.string().optional(),
})
type SpecialtyForm = z.infer<typeof specialtySchema>

export const AdminSpecialtiesPage: React.FC = () => {
  const [specialties, setSpecialties] = useState<Specialty[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<Specialty | null>(null)
  const [deleting, setDeleting] = useState<number | null>(null)

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } =
    useForm<SpecialtyForm>({ resolver: zodResolver(specialtySchema) })

  const load = () => {
    specialtyService.getAll()
      .then((r) => setSpecialties(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const openCreate = () => { setEditing(null); reset({}); setModalOpen(true) }
  const openEdit = (s: Specialty) => {
    setEditing(s)
    reset({ name: s.name, description: s.description, iconUrl: s.iconUrl })
    setModalOpen(true)
  }

  const onSubmit = async (data: SpecialtyForm) => {
    try {
      if (editing) {
        await specialtyService.update(editing.specialtyId, data)
        toast.success('Cập nhật chuyên khoa thành công')
      } else {
        await specialtyService.create(data as any)
        toast.success('Tạo chuyên khoa thành công')
      }
      setModalOpen(false)
      load()
    } catch {}
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Xoá chuyên khoa này?')) return
    setDeleting(id)
    try {
      await specialtyService.delete(id)
      toast.success('Đã xoá chuyên khoa')
      load()
    } catch {
    } finally {
      setDeleting(null)
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Quản lý chuyên khoa</h1>
          <p className="text-gray-500 mt-1">{specialties.length} chuyên khoa</p>
        </div>
        <Button onClick={openCreate}><Plus size={16} /> Thêm mới</Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {specialties.map((s) => (
            <div key={s.specialtyId} className="card p-4 flex items-start justify-between gap-3">
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-gray-900">{s.name}</p>
                {s.description && (
                  <p className="text-sm text-gray-500 mt-1 line-clamp-2">{s.description}</p>
                )}
              </div>
              <div className="flex gap-1 flex-shrink-0">
                <button
                  onClick={() => openEdit(s)}
                  className="p-2 rounded-lg text-gray-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
                >
                  <Edit2 size={15} />
                </button>
                <button
                  onClick={() => handleDelete(s.specialtyId)}
                  disabled={deleting === s.specialtyId}
                  className="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Cập nhật chuyên khoa' : 'Thêm chuyên khoa'}
        footer={
          <>
            <Button variant="secondary" onClick={() => setModalOpen(false)}>Huỷ</Button>
            <Button loading={isSubmitting} onClick={handleSubmit(onSubmit)}>
              {editing ? 'Cập nhật' : 'Tạo mới'}
            </Button>
          </>
        }
      >
        <form className="space-y-4">
          <Input
            {...register('name')}
            label="Tên chuyên khoa"
            placeholder="vd: Tim mạch, Da liễu..."
            error={errors.name?.message}
          />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Mô tả</label>
            <textarea
              {...register('description')}
              rows={3}
              className="input resize-none"
              placeholder="Mô tả ngắn về chuyên khoa..."
            />
          </div>
          <Input
            {...register('iconUrl')}
            label="URL icon (tuỳ chọn)"
            placeholder="https://..."
          />
        </form>
      </Modal>
    </div>
  )
}
