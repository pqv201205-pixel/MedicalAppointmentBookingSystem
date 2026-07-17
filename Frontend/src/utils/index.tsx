import type { AppointmentStatus } from '@/types'

// ─── Status badge ─────────────────────────────────────────────────────────────
export const statusConfig: Record<AppointmentStatus, { label: string; className: string }> = {
  PENDING:    { label: 'Chờ xác nhận', className: 'badge-pending' },
  CONFIRMED:  { label: 'Đã xác nhận',  className: 'badge-confirmed' },
  CHECKED_IN: { label: 'Đã check-in',  className: 'badge-checkin' },
  COMPLETED:  { label: 'Hoàn thành',   className: 'badge-completed' },
  CANCELLED:  { label: 'Đã hủy',       className: 'badge-cancelled' },
  NO_SHOW:    { label: 'Không đến',     className: 'badge-noshow' },
}

// ─── Date helpers ─────────────────────────────────────────────────────────────
export const formatDate = (date: string) =>
  new Date(date).toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })

export const formatDateTime = (date: string) =>
  new Date(date).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })

export const toInputDate = (date: Date) =>
  date.toISOString().split('T')[0]

export const getTodayString = () => toInputDate(new Date())

export const getMinBookingDate = () => {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return toInputDate(d)
}

// ─── Currency ─────────────────────────────────────────────────────────────────
export const formatCurrency = (amount: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)

// ─── Role label ───────────────────────────────────────────────────────────────
export const roleLabel = (role: string) =>
  ({ ADMIN: 'Quản trị viên', DOCTOR: 'Bác sĩ', PATIENT: 'Bệnh nhân' }[role] ?? role)
