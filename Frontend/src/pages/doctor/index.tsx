import React, { useEffect, useState } from 'react'
import { Calendar, Clock, Users, CheckCircle, Plus, Trash2, Edit2, ChevronDown } from 'lucide-react'
import { doctorService, appointmentService } from '@/services'
import type { Appointment, DoctorSchedule, Doctor } from '@/types'
import { Button, StatusBadge, EmptyState, Spinner, Input, Select, Modal } from '@/components/common'
import { formatDate, getTodayString } from '@/utils'
import toast from 'react-hot-toast'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'

// ─── Doctor Dashboard ─────────────────────────────────────────────────────────
export const DoctorDashboard: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)
  const [today] = useState(getTodayString())

  useEffect(() => {
    doctorService.getMyAppointments(today)
      .then((r) => setAppointments(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [today])

  const stats = {
    total:     appointments.length,
    pending:   appointments.filter((a) => a.status === 'PENDING').length,
    confirmed: appointments.filter((a) => a.status === 'CONFIRMED').length,
    completed: appointments.filter((a) => a.status === 'COMPLETED').length,
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Bảng điều khiển</h1>
        <p className="text-gray-500 mt-1">Lịch hẹn hôm nay — {formatDate(today)}</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { label: 'Tổng hôm nay',   value: stats.total,     color: 'bg-blue-50   text-blue-600' },
          { label: 'Chờ xác nhận',   value: stats.pending,   color: 'bg-yellow-50 text-yellow-600' },
          { label: 'Đã xác nhận',    value: stats.confirmed, color: 'bg-green-50  text-green-600' },
          { label: 'Hoàn thành',     value: stats.completed, color: 'bg-purple-50 text-purple-600' },
        ].map(({ label, value, color }) => (
          <div key={label} className="card p-4">
            <p className={`text-3xl font-bold ${color.split(' ')[1]}`}>{value}</p>
            <p className="text-sm text-gray-500 mt-1">{label}</p>
          </div>
        ))}
      </div>

      {/* Today appointments */}
      <div className="card p-5">
        <h2 className="font-semibold text-gray-900 mb-4">Lịch hẹn hôm nay</h2>
        {loading ? (
          <div className="flex justify-center py-8"><Spinner /></div>
        ) : appointments.length === 0 ? (
          <EmptyState icon={<Calendar size={40} />} title="Không có lịch hẹn hôm nay" />
        ) : (
          <div className="space-y-3">
            {appointments.map((a) => (
              <AppointmentRow key={a.appointmentId} appointment={a} onRefresh={() =>
                doctorService.getMyAppointments(today).then((r) => setAppointments(r.data.data))
              } />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

// ─── Appointment Row — dùng chung ─────────────────────────────────────────────
const AppointmentRow: React.FC<{
  appointment: Appointment
  onRefresh: () => void
}> = ({ appointment: a, onRefresh }) => {
  const [updating, setUpdating] = useState(false)

  const nextStatus: Record<string, string> = {
    PENDING:   'CONFIRMED',
    CONFIRMED: 'CHECKED_IN',
    CHECKED_IN:'COMPLETED',
  }

  const nextLabel: Record<string, string> = {
    PENDING:   'Xác nhận',
    CONFIRMED: 'Check-in',
    CHECKED_IN:'Hoàn thành',
  }

  const handleUpdate = async (status: string) => {
    setUpdating(true)
    try {
      await appointmentService.updateStatus(a.appointmentId, status)
      toast.success('Cập nhật trạng thái thành công')
      onRefresh()
    } catch {
    } finally {
      setUpdating(false)
    }
  }

  const next = nextStatus[a.status]

  return (
    <div className="flex items-center gap-4 p-3 bg-gray-50 rounded-xl">
      <div className="text-center min-w-[48px]">
        <p className="text-xs text-gray-400">Giờ</p>
        <p className="font-bold text-gray-900">{a.timeSlot}</p>
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-medium text-gray-900 truncate">{a.patient.fullName}</p>
        <p className="text-sm text-gray-500 truncate">{a.symptoms || 'Không có triệu chứng'}</p>
      </div>
      <StatusBadge status={a.status} />
      {next && (
        <Button size="sm" loading={updating} onClick={() => handleUpdate(next)}>
          {nextLabel[a.status]}
        </Button>
      )}
    </div>
  )
}

// ─── Doctor Appointments ──────────────────────────────────────────────────────
export const DoctorAppointmentsPage: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)
  const [date, setDate] = useState(getTodayString())

  const load = (d?: string) => {
    setLoading(true)
    doctorService.getMyAppointments(d)
      .then((r) => setAppointments(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load(date) }, [date])

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <h1 className="text-2xl font-bold text-gray-900">Lịch hẹn</h1>
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          className="input w-40"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : appointments.length === 0 ? (
        <EmptyState icon={<Calendar size={48} />} title="Không có lịch hẹn" desc="Chọn ngày khác để xem" />
      ) : (
        <div className="space-y-3">
          {appointments.map((a) => (
            <div key={a.appointmentId} className="card p-4">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 bg-blue-50 rounded-xl flex flex-col items-center justify-center flex-shrink-0">
                  <Clock size={16} className="text-blue-500" />
                  <span className="text-sm font-bold text-blue-700">{a.timeSlot}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2 flex-wrap">
                    <div>
                      <p className="font-semibold text-gray-900">{a.patient.fullName}</p>
                      <p className="text-sm text-gray-500">{a.patient.gender} · {formatDate(a.patient.dateOfBirth)}</p>
                    </div>
                    <StatusBadge status={a.status} />
                  </div>
                  {a.symptoms && (
                    <div className="mt-2 p-2.5 bg-orange-50 rounded-lg">
                      <p className="text-xs font-medium text-orange-700">Triệu chứng</p>
                      <p className="text-sm text-orange-600">{a.symptoms}</p>
                    </div>
                  )}
                  {nextStatus[a.status] && (
                    <div className="mt-3 flex gap-2">
                      <Button
                        size="sm"
                        onClick={() => appointmentService.updateStatus(a.appointmentId, nextStatus[a.status])
                          .then(() => { toast.success('Cập nhật thành công'); load(date) })
                          .catch(() => {})}
                      >
                        {nextLabel[a.status]}
                      </Button>
                      {a.status === 'PENDING' && (
                        <Button
                          size="sm"
                          variant="secondary"
                          onClick={() => appointmentService.updateStatus(a.appointmentId, 'CANCELLED')
                            .then(() => { toast.success('Đã từ chối'); load(date) })
                            .catch(() => {})}
                        >
                          Từ chối
                        </Button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

const nextStatus: Record<string, string> = {
  PENDING: 'CONFIRMED', CONFIRMED: 'CHECKED_IN', CHECKED_IN: 'COMPLETED',
}
const nextLabel: Record<string, string> = {
  PENDING: 'Xác nhận', CONFIRMED: 'Check-in', CHECKED_IN: 'Hoàn thành',
}

// ─── Schedule form schema ─────────────────────────────────────────────────────
const scheduleSchema = z.object({
  dayOfWeek: z.coerce.number().min(1).max(7),
  startTime: z.string().min(1, 'Vui lòng chọn giờ bắt đầu'),
  endTime:   z.string().min(1, 'Vui lòng chọn giờ kết thúc'),
  maxSlots:  z.coerce.number().min(1).max(50),
})
type ScheduleForm = z.infer<typeof scheduleSchema>

// ─── Doctor Schedules ─────────────────────────────────────────────────────────
export const DoctorSchedulesPage: React.FC = () => {
  const [schedules, setSchedules] = useState<DoctorSchedule[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<DoctorSchedule | null>(null)
  const [deleting, setDeleting] = useState<number | null>(null)

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } =
    useForm<ScheduleForm>({ resolver: zodResolver(scheduleSchema) })

  const loadSchedules = () => {
    // lấy doctorId từ profile trước
    doctorService.getMyProfile().then((r) => {
      doctorService.getSchedules(r.data.data.doctorId).then((res) => {
        setSchedules(res.data.data)
        setLoading(false)
      })
    }).catch(() => setLoading(false))
  }

  useEffect(() => { loadSchedules() }, [])

  const openCreate = () => { setEditing(null); reset({}); setModalOpen(true) }
  const openEdit = (s: DoctorSchedule) => {
    setEditing(s)
    reset({ dayOfWeek: s.dayOfWeek, startTime: s.startTime, endTime: s.endTime, maxSlots: s.maxSlots })
    setModalOpen(true)
  }

  const onSubmit = async (data: ScheduleForm) => {
    try {
      if (editing) {
        await doctorService.updateSchedule(editing.scheduleId, data)
        toast.success('Cập nhật lịch làm việc thành công')
      } else {
        await doctorService.createSchedule(data as any)
        toast.success('Tạo lịch làm việc thành công')
      }
      setModalOpen(false)
      loadSchedules()
    } catch {}
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Xoá lịch làm việc này?')) return
    setDeleting(id)
    try {
      await doctorService.deleteSchedule(id)
      toast.success('Đã xoá lịch làm việc')
      loadSchedules()
    } catch {
    } finally {
      setDeleting(null)
    }
  }

  const dayLabels = ['', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ nhật']

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Lịch làm việc</h1>
          <p className="text-gray-500 mt-1">Quản lý thời gian khám bệnh</p>
        </div>
        <Button onClick={openCreate}>
          <Plus size={16} /> Thêm lịch
        </Button>
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : schedules.length === 0 ? (
        <EmptyState
          icon={<Clock size={48} />}
          title="Chưa có lịch làm việc"
          desc="Thêm lịch làm việc để bệnh nhân có thể đặt lịch"
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {schedules.map((s) => (
            <div key={s.scheduleId} className="card p-4">
              <div className="flex items-start justify-between">
                <div>
                  <span className="inline-block bg-blue-100 text-blue-700 text-xs font-semibold px-2.5 py-0.5 rounded-full mb-2">
                    {dayLabels[s.dayOfWeek]}
                  </span>
                  <p className="text-lg font-semibold text-gray-900">
                    {s.startTime} — {s.endTime}
                  </p>
                  <p className="text-sm text-gray-500 flex items-center gap-1 mt-1">
                    <Users size={13} /> Tối đa {s.maxSlots} bệnh nhân
                  </p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => openEdit(s)}
                    className="p-2 rounded-lg text-gray-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button
                    onClick={() => handleDelete(s.scheduleId)}
                    className="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"
                    disabled={deleting === s.scheduleId}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal create/edit */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Cập nhật lịch làm việc' : 'Thêm lịch làm việc'}
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
          <Select
            {...register('dayOfWeek')}
            label="Ngày trong tuần"
            error={errors.dayOfWeek?.message}
            options={[1,2,3,4,5,6,7].map((d) => ({ value: d, label: dayLabels[d] }))}
            placeholder="Chọn ngày"
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              {...register('startTime')}
              type="time"
              label="Giờ bắt đầu"
              error={errors.startTime?.message}
            />
            <Input
              {...register('endTime')}
              type="time"
              label="Giờ kết thúc"
              error={errors.endTime?.message}
            />
          </div>
          <Input
            {...register('maxSlots')}
            type="number"
            label="Số bệnh nhân tối đa"
            min={1}
            max={50}
            error={errors.maxSlots?.message}
          />
        </form>
      </Modal>
    </div>
  )
}

// ─── Doctor Profile ───────────────────────────────────────────────────────────
const profileSchema = z.object({
  fullName:        z.string().min(1, 'Không được để trống'),
  specialty:       z.string().min(1, 'Không được để trống'),
  degree:          z.string().optional(),
  experienceYears: z.coerce.number().min(0).max(60),
  consultationFee: z.coerce.number().min(0),
  biography:       z.string().optional(),
})
type ProfileForm = z.infer<typeof profileSchema>

export const DoctorProfilePage: React.FC = () => {
  const [doctor, setDoctor] = useState<Doctor | null>(null)
  const [loading, setLoading] = useState(true)
  const { register, handleSubmit, reset, formState: { errors, isSubmitting, isDirty } } =
    useForm<ProfileForm>({ resolver: zodResolver(profileSchema) })

  useEffect(() => {
    doctorService.getMyProfile().then((r) => {
      const d = r.data.data
      setDoctor(d)
      reset({
        fullName:        d.fullName,
        specialty:       d.specialty,
        degree:          d.degree,
        experienceYears: d.experienceYears,
        consultationFee: d.consultationFee,
        biography:       d.biography,
      })
    }).finally(() => setLoading(false))
  }, [reset])

  const onSubmit = async (data: ProfileForm) => {
    try {
      const res = await doctorService.updateMyProfile(data)
      setDoctor(res.data.data)
      reset(data)
      toast.success('Cập nhật hồ sơ thành công')
    } catch {}
  }

  if (loading) return <div className="flex justify-center py-16"><Spinner /></div>

  return (
    <div className="max-w-2xl space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Hồ sơ của tôi</h1>

      <div className="card p-6 space-y-5">
        {/* Avatar */}
        <div className="flex items-center gap-4">
          <div className="w-20 h-20 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-3xl">
            {doctor?.fullName[0]}
          </div>
          <div>
            <p className="font-semibold text-gray-900 text-lg">BS. {doctor?.fullName}</p>
            <p className="text-blue-600">{doctor?.specialty}</p>
            <p className="text-sm text-gray-400">{doctor?.user.email}</p>
          </div>
        </div>

        <hr className="border-gray-100" />

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input {...register('fullName')} label="Họ và tên" error={errors.fullName?.message} />
            <Input {...register('specialty')} label="Chuyên khoa" error={errors.specialty?.message} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input {...register('degree')} label="Học vị" placeholder="BS., ThS., TS.BS..." />
            <Input {...register('experienceYears')} type="number" label="Số năm kinh nghiệm" error={errors.experienceYears?.message} />
          </div>
          <Input
            {...register('consultationFee')}
            type="number"
            label="Phí tư vấn (VND)"
            error={errors.consultationFee?.message}
          />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Giới thiệu bản thân</label>
            <textarea
              {...register('biography')}
              rows={4}
              className="input resize-none"
              placeholder="Mô tả kinh nghiệm, chuyên môn..."
            />
          </div>

          <Button type="submit" loading={isSubmitting} disabled={!isDirty}>
            Lưu thay đổi
          </Button>
        </form>
      </div>
    </div>
  )
}
