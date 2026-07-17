import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Calendar, Clock, UserCheck, History, ChevronRight, Search, Filter } from 'lucide-react'
import { patientService, doctorService, appointmentService, specialtyService } from '@/services'
import type { Appointment, Doctor, Specialty } from '@/types'
import { Button, StatusBadge, EmptyState, Spinner, Input, Select, Modal, Textarea } from '@/components/common'
import { formatDate, formatCurrency, getMinBookingDate } from '@/utils'
import toast from 'react-hot-toast'

// ─── Patient Dashboard ────────────────────────────────────────────────────────
export const PatientDashboard: React.FC = () => {
  const navigate = useNavigate()
  const [upcoming, setUpcoming] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    patientService.getUpcomingAppointments()
      .then((r) => setUpcoming(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Xin chào! 👋</h1>
        <p className="text-gray-500 mt-1">Quản lý lịch khám bệnh của bạn</p>
      </div>

      {/* Quick stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { icon: Calendar,    label: 'Lịch sắp tới', value: upcoming.filter(a => ['PENDING','CONFIRMED'].includes(a.status)).length, color: 'bg-blue-50 text-blue-600' },
          { icon: UserCheck,   label: 'Đã xác nhận',  value: upcoming.filter(a => a.status === 'CONFIRMED').length, color: 'bg-green-50 text-green-600' },
          { icon: Clock,       label: 'Chờ xác nhận', value: upcoming.filter(a => a.status === 'PENDING').length, color: 'bg-yellow-50 text-yellow-600' },
          { icon: History,     label: 'Tổng lịch hẹn', value: upcoming.length, color: 'bg-purple-50 text-purple-600' },
        ].map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="card p-4">
            <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 ${color}`}>
              <Icon size={20} />
            </div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-sm text-gray-500">{label}</p>
          </div>
        ))}
      </div>

      {/* Quick actions */}
      <div className="card p-5">
        <h2 className="font-semibold text-gray-900 mb-4">Thao tác nhanh</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <button
            onClick={() => navigate('/patient/doctors')}
            className="flex items-center gap-3 p-4 bg-blue-600 rounded-xl text-white hover:bg-blue-700 transition-colors"
          >
            <Search size={20} />
            <div className="text-left">
              <p className="font-medium">Tìm bác sĩ & Đặt lịch</p>
              <p className="text-xs text-blue-100">Chọn chuyên khoa, bác sĩ, khung giờ</p>
            </div>
            <ChevronRight size={18} className="ml-auto" />
          </button>
          <button
            onClick={() => navigate('/patient/appointments')}
            className="flex items-center gap-3 p-4 bg-gray-50 border border-gray-100 rounded-xl text-gray-700 hover:bg-gray-100 transition-colors"
          >
            <Calendar size={20} />
            <div className="text-left">
              <p className="font-medium">Xem lịch hẹn</p>
              <p className="text-xs text-gray-500">Lịch sắp tới & lịch sử khám</p>
            </div>
            <ChevronRight size={18} className="ml-auto" />
          </button>
        </div>
      </div>

      {/* Upcoming appointments */}
      <div className="card p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-900">Lịch hẹn sắp tới</h2>
          <button onClick={() => navigate('/patient/appointments')} className="text-sm text-blue-600 hover:underline">
            Xem tất cả
          </button>
        </div>
        {loading ? (
          <div className="flex justify-center py-8"><Spinner /></div>
        ) : upcoming.length === 0 ? (
          <EmptyState icon={<Calendar size={40} />} title="Chưa có lịch hẹn nào" desc="Đặt lịch khám để bắt đầu" />
        ) : (
          <div className="space-y-3">
            {upcoming.slice(0, 3).map((a) => (
              <div key={a.appointmentId} className="flex items-center gap-4 p-3 bg-gray-50 rounded-xl">
                <div className="w-12 h-12 bg-blue-100 rounded-xl flex flex-col items-center justify-center flex-shrink-0">
                  <span className="text-xs text-blue-600 font-medium">
                    {new Date(a.appointmentDate).toLocaleDateString('vi-VN', { month: 'short' })}
                  </span>
                  <span className="text-lg font-bold text-blue-700 leading-none">
                    {new Date(a.appointmentDate).getDate()}
                  </span>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-900 truncate">BS. {a.doctor.fullName}</p>
                  <p className="text-sm text-gray-500">{a.doctor.specialty} · {a.timeSlot}</p>
                </div>
                <StatusBadge status={a.status} />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

// ─── Doctors List ─────────────────────────────────────────────────────────────
export const DoctorsListPage: React.FC = () => {
  const navigate = useNavigate()
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [specialties, setSpecialties] = useState<Specialty[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [selectedSpecialty, setSelectedSpecialty] = useState('')

  useEffect(() => {
    let isMounted = true
    setLoading(true)
    
    Promise.all([
      doctorService.getDoctors().catch(err => { console.error("Lỗi API Doctors:", err); return null }),
      specialtyService.getAll().catch(err => { console.error("Lỗi API Specialties:", err); return null })
    ])
      .then(([dRes, sRes]) => {
        if (!isMounted) return

        // Kiểm tra mọi tầng cấu trúc dữ liệu có thể có
        const doctorsData = dRes?.data?.data || dRes?.data || dRes || []
        const specialtiesData = sRes?.data?.data || sRes?.data || sRes || []
        
        setDoctors(Array.isArray(doctorsData) ? doctorsData : [])
        setSpecialties(Array.isArray(specialtiesData) ? specialtiesData : [])
      })
      .catch((error) => {
        console.error("Lỗi tổng hợp:", error)
        toast.error("Không thể kết nối dữ liệu hệ thống")
      })
      .finally(() => {
        if (isMounted) setLoading(false)
      })

    return () => { isMounted = false }
  }, [])

  // Biến bảo vệ lớp 2: Ép kiểu mảng chắc chắn trước khi Filter/Map
  const safeDoctors = Array.isArray(doctors) ? doctors : []
  const safeSpecialties = Array.isArray(specialties) ? specialties : []

  const filtered = safeDoctors.filter((d) => {
    if (!d || !d.fullName) return false
    const matchName = d.fullName.toLowerCase().includes(search.toLowerCase())
    const matchSpec = !selectedSpecialty || d.specialty === selectedSpecialty
    return matchName && matchSpec
  })

  // Khớp nối cấu trúc options cho component Select custom của bạn
  const specialtyOptions = safeSpecialties
    .filter(s => s && s.name)
    .map(s => ({
      value: s.name,
      label: s.name
    }))

  return (
    <div className="space-y-5 p-4">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Tìm bác sĩ</h1>
        <p className="text-gray-500 mt-1">Chọn bác sĩ phù hợp để đặt lịch khám</p>
      </div>

      {/* Bộ lọc chuẩn hóa sử dụng các component viết hoa từ common */}
      <div className="flex gap-3 flex-wrap">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 z-10" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Tìm theo tên bác sĩ..."
            className="pl-9"
          />
        </div>
        
        <Select
          value={selectedSpecialty}
          onChange={(e) => setSelectedSpecialty(e.target.value)}
          options={specialtyOptions}
          placeholder="Tất cả chuyên khoa"
          className="w-48 bg-white"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner size={36} /></div>
      ) : filtered.length === 0 ? (
        <EmptyState icon={<Search size={40} />} title="Không tìm thấy bác sĩ" desc="Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm" />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {filtered.map((doctor) => {
            if (!doctor || !doctor.doctorId) return null
            return (
              <div key={doctor.doctorId} className="card p-5 hover:shadow-md transition-shadow bg-white rounded-xl border border-gray-100">
                <div className="flex items-start gap-3 mb-4">
                  <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-lg flex-shrink-0">
                    {doctor.fullName ? doctor.fullName[0].toUpperCase() : 'D'}
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-gray-900 truncate">BS. {doctor.fullName}</h3>
                    <p className="text-sm text-blue-600 truncate">{doctor.specialty || 'Chuyên khoa'}</p>
                    <p className="text-xs text-gray-400 mt-0.5">
                      {doctor.degree || 'Bác sĩ'} · {doctor.experienceYears || 0} năm kinh nghiệm
                    </p>
                  </div>
                </div>
                <div className="flex items-center justify-between pt-3 border-t border-gray-100 mt-4">
                  <div>
                    <p className="text-xs text-gray-400">Phí tư vấn</p>
                    <p className="font-semibold text-gray-900 text-sm">
                      {formatCurrency ? formatCurrency(doctor.consultationFee) : `${doctor.consultationFee?.toLocaleString()} đ`}
                    </p>
                  </div>
                  <Button size="sm" onClick={() => navigate(`/patient/book/${doctor.doctorId}`)}>
                    Đặt lịch
                  </Button>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

// ─── Book Appointment ─────────────────────────────────────────────────────────
import { useParams } from 'react-router-dom'

export const BookAppointmentPage: React.FC = () => {
  const { doctorId } = useParams<{ doctorId: string }>()
  const navigate = useNavigate()
  const [doctor, setDoctor] = useState<Doctor | null>(null)
  const [slots, setSlots] = useState<string[]>([])
  const [selectedDate, setSelectedDate] = useState('')
  const [selectedSlot, setSelectedSlot] = useState('')
  const [symptoms, setSymptoms] = useState('')
  const [notes, setNotes] = useState('')
  const [loading, setLoading] = useState(false)
  const [loadingSlots, setLoadingSlots] = useState(false)

  useEffect(() => {
    if (doctorId) {
      doctorService.getDoctorById(Number(doctorId))
        .then((r) => setDoctor(r.data.data))
        .catch(() => navigate('/patient/doctors'))
    }
  }, [doctorId])

  useEffect(() => {
    if (selectedDate && doctorId) {
      setLoadingSlots(true)
      setSelectedSlot('')
      doctorService.getAvailableSlots(Number(doctorId), selectedDate)
        .then((r) => setSlots(r.data.data))
        .catch(() => setSlots([]))
        .finally(() => setLoadingSlots(false))
    }
  }, [selectedDate, doctorId])

  const handleBook = async () => {
    if (!selectedDate || !selectedSlot) {
      toast.error('Vui lòng chọn ngày và khung giờ')
      return
    }
    setLoading(true)
    try {
      await appointmentService.book({
        doctorId: Number(doctorId),
        appointmentDate: selectedDate,
        timeSlot: selectedSlot,
        symptoms, notes,
      })
      toast.success('Đặt lịch thành công!')
      navigate('/patient/appointments')
    } catch {
    } finally {
      setLoading(false)
    }
  }

  if (!doctor) return <div className="flex justify-center py-16"><Spinner /></div>

  return (
    <div className="max-w-2xl mx-auto space-y-5">
      <div>
        <button onClick={() => navigate(-1)} className="text-sm text-gray-500 hover:text-gray-700 mb-2 flex items-center gap-1">
          ← Quay lại
        </button>
        <h1 className="text-2xl font-bold text-gray-900">Đặt lịch khám</h1>
      </div>

      {/* Doctor info */}
      <div className="card p-5">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-2xl">
            {doctor.fullName[0]}
          </div>
          <div>
            <h2 className="text-lg font-semibold text-gray-900">BS. {doctor.fullName}</h2>
            <p className="text-blue-600">{doctor.specialty}</p>
            <p className="text-sm text-gray-500">{doctor.degree} · {doctor.experienceYears} năm kinh nghiệm</p>
          </div>
        </div>
        {doctor.biography && (
          <p className="text-sm text-gray-600 mt-4 leading-relaxed">{doctor.biography}</p>
        )}
      </div>

      {/* Booking form */}
      <div className="card p-5 space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Chọn ngày khám</label>
          <input
            type="date"
            min={getMinBookingDate()}
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="input"
          />
        </div>

        {selectedDate && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Chọn khung giờ</label>
            {loadingSlots ? (
              <div className="flex justify-center py-4"><Spinner /></div>
            ) : slots.length === 0 ? (
              <p className="text-sm text-gray-400 bg-gray-50 rounded-lg p-4 text-center">
                Không có slot trống cho ngày này
              </p>
            ) : (
              <div className="grid grid-cols-4 sm:grid-cols-6 gap-2">
                {slots.map((slot) => (
                  <button
                    key={slot}
                    onClick={() => setSelectedSlot(slot)}
                    className={`py-2 px-3 rounded-lg text-sm font-medium border transition-all
                      ${selectedSlot === slot
                        ? 'bg-blue-600 text-white border-blue-600'
                        : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300'}`}
                  >
                    {slot}
                  </button>
                ))}
              </div>
            )}
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Triệu chứng</label>
          <textarea
            value={symptoms}
            onChange={(e) => setSymptoms(e.target.value)}
            rows={3}
            placeholder="Mô tả triệu chứng, lý do khám..."
            className="input resize-none"
            maxLength={500}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1.5">Ghi chú thêm</label>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            rows={2}
            placeholder="Thông tin thêm cho bác sĩ..."
            className="input resize-none"
            maxLength={500}
          />
        </div>

        {/* Summary */}
        {selectedDate && selectedSlot && (
          <div className="bg-blue-50 rounded-xl p-4 space-y-1">
            <p className="text-sm font-medium text-blue-900">Xác nhận thông tin</p>
            <p className="text-sm text-blue-700">📅 {formatDate(selectedDate)} lúc {selectedSlot}</p>
            <p className="text-sm text-blue-700">👨‍⚕️ BS. {doctor.fullName} — {doctor.specialty}</p>
            <p className="text-sm text-blue-700">💰 {formatCurrency(doctor.consultationFee)}</p>
          </div>
        )}

        <Button
          className="w-full"
          size="lg"
          loading={loading}
          onClick={handleBook}
          disabled={!selectedDate || !selectedSlot}
        >
          Xác nhận đặt lịch
        </Button>
      </div>
    </div>
  )
}

// ─── Patient Appointments ─────────────────────────────────────────────────────
export const PatientAppointmentsPage: React.FC = () => {
  const [upcoming, setUpcoming] = useState<Appointment[]>([])
  const [history, setHistory] = useState<Appointment[]>([])
  const [tab, setTab] = useState<'upcoming' | 'history'>('upcoming')
  const [loading, setLoading] = useState(true)
  const [cancelling, setCancelling] = useState<number | null>(null)

  const loadData = () => {
    setLoading(true)
    Promise.all([
      patientService.getUpcomingAppointments(),
      patientService.getAppointmentHistory(),
    ]).then(([u, h]) => {
      setUpcoming(u.data.data)
      setHistory(h.data.data)
    }).finally(() => setLoading(false))
  }

  useEffect(() => { loadData() }, [])

  const handleCancel = async (id: number) => {
    if (!confirm('Bạn có chắc muốn hủy lịch này?')) return
    setCancelling(id)
    try {
      await appointmentService.cancel(id)
      toast.success('Hủy lịch thành công')
      loadData()
    } catch {
    } finally {
      setCancelling(null)
    }
  }

  const list = tab === 'upcoming' ? upcoming : history

  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Lịch hẹn của tôi</h1>

      <div className="flex gap-2 border-b border-gray-100">
        {(['upcoming', 'history'] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2.5 text-sm font-medium border-b-2 transition-colors
              ${tab === t
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'}`}
          >
            {t === 'upcoming' ? `Sắp tới (${upcoming.length})` : `Lịch sử (${history.length})`}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : list.length === 0 ? (
        <EmptyState icon={<Calendar size={48} />} title="Không có lịch hẹn nào" />
      ) : (
        <div className="space-y-3">
          {list.map((a) => (
            <div key={a.appointmentId} className="card p-4">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3 flex-1 min-w-0">
                  <div className="w-12 h-12 bg-blue-50 rounded-xl flex flex-col items-center justify-center flex-shrink-0">
                    <span className="text-xs text-blue-500">
                      {new Date(a.appointmentDate).toLocaleDateString('vi-VN', { month: 'short' })}
                    </span>
                    <span className="text-lg font-bold text-blue-700 leading-tight">
                      {new Date(a.appointmentDate).getDate()}
                    </span>
                  </div>
                  <div className="min-w-0">
                    <p className="font-semibold text-gray-900">BS. {a.doctor.fullName}</p>
                    <p className="text-sm text-gray-500">{a.doctor.specialty}</p>
                    <p className="text-sm text-gray-400 flex items-center gap-1 mt-0.5">
                      <Clock size={13} /> {a.timeSlot}
                    </p>
                    {a.symptoms && (
                      <p className="text-xs text-gray-400 mt-1 truncate">💊 {a.symptoms}</p>
                    )}
                  </div>
                </div>
                <div className="flex flex-col items-end gap-2">
                  <StatusBadge status={a.status} />
                  {['PENDING', 'CONFIRMED'].includes(a.status) && (
                    <Button
                      variant="danger"
                      size="sm"
                      loading={cancelling === a.appointmentId}
                      onClick={() => handleCancel(a.appointmentId)}
                    >
                      Hủy
                    </Button>
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
