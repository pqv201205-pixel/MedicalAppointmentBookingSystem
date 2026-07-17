// ─── ApiResponse ─────────────────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

// ─── Auth ─────────────────────────────────────────────────────────────────────
export interface User {
  userId: number
  username: string
  email: string
  phoneNumber: string
  role: 'ADMIN' | 'DOCTOR' | 'PATIENT'
  isActive: boolean
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  phoneNumber: string
  role?: string
}

// ─── Patient ──────────────────────────────────────────────────────────────────
export interface Patient {
  patientId: number
  fullName: string
  gender: string
  dateOfBirth: string // YYYY-MM-DD
  address: string
  medicalHistory: string
  user: User
}

// Đồng bộ với PatientRequest ở Backend (Dùng khi cập nhật thông tin)
export interface PatientRequest {
  fullName: string
  gender: string
  dateOfBirth: string
  address: string
  medicalHistory?: string
}

// ─── Doctor ───────────────────────────────────────────────────────────────────
export interface Doctor {
  doctorId: number
  fullName: string
  specialty: string
  degree: string
  experienceYears: number
  consultationFee: number
  biography: string
  user: User
}

export interface DoctorSchedule {
  scheduleId: number
  doctorId: number
  doctorName: string
  dayOfWeek: number
  dayOfWeekLabel: string
  startTime: string
  endTime: string
  maxSlots: number
}

// MỚI BỔ SUNG: Đồng bộ với ScheduleResponse từ DoctorScheduleController
export interface ScheduleResponse {
  scheduleId: number
  startTime: string
  endTime: string
  availableSlots: number
  isReserved: boolean
}

// ─── Appointment ──────────────────────────────────────────────────────────────
export type AppointmentStatus =
  | 'PENDING' | 'CONFIRMED' | 'CHECKED_IN'
  | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW'

export interface Appointment {
  appointmentId: number
  patient: Patient
  doctor: Doctor
  appointmentDate: string // YYYY-MM-DD
  timeSlot: string
  status: AppointmentStatus
  paymentStatus: string
  symptoms: string
  notes: string
  createdAt: string
  updatedAt: string
}

// Đồng bộ với AppointmentResponse ở Backend (Trả về từ getUpcomingAppointments / getAppointmentHistory)
export interface AppointmentResponse {
  id: number
  date: string
  startTime: string
  endTime: string
  status: AppointmentStatus
  symptoms: string
  qrCode?: string
  doctorId: number
  doctorName: string
}

// ─── Specialty ────────────────────────────────────────────────────────────────
export interface Specialty {
  specialtyId: number
  name: string
  description: string
  iconUrl: string
}

// MỚI BỔ SUNG: Đồng bộ với SpecializationRequest phục vụ Admin Create/Update
export interface SpecializationRequest {
  name: string
  description: string
}

// ─── Notification ─────────────────────────────────────────────────────────────
export interface Notification {
  notificationId: number
  title: string
  message: string
  isRead: boolean
  notificationType: string
  createdAt: string
}

// ─── Dashboard ────────────────────────────────────────────────────────────────
export interface DashboardSummary {
  totalPatients: number
  totalDoctors: number
  totalAppointments: number
  completedAppointments: number
  cancelledAppointments: number
  pendingAppointments: number
  confirmedAppointments: number
  cancelRate: number
  monthlyStats: MonthlyStats[]
}

export interface MonthlyStats {
  month: string
  total: number
  completed: number
  cancelled: number
}

// ==============================================================================
// ─── PHẦN MỚI BỔ SUNG ĐỒNG BỘ 100% THEO FILE CONTROLLER CỦA BẠN ───
// ==============================================================================

// ─── Medical Record (Đồng bộ với MedicalRecordController & MedicalRecord Entity) ───
export interface MedicalRecordRequest {
  appointmentId: number
  patientId: number
  diagnosis: string
  treatmentPlan: string
  prescription?: string
}

export interface MedicalRecord {
  recordId: number
  appointmentId: number
  patientId: number
  diagnosis: string
  treatmentPlan: string
  prescription?: string
  createdAt: string
  // Nếu Backend trả về nguyên Entity chứa các Object quan hệ thì dùng 2 trường dưới, nếu không có thể ẩn đi
  patient?: Patient
  appointment?: Appointment
}

// ─── Review (Đồng bộ với ReviewController & Review Entity) ─────────────────────
export interface ReviewRequest {
  appointmentId: number
  rating: number // từ 1 đến 5 sao
  comment: string
}

export interface Review {
  reviewId: number
  appointmentId: number
  doctorId: number
  patientName: string
  rating: number
  comment: string
  createdAt: string
}

// ─── Waiting List (Đồng bộ với WaitingListController & WaitingRequest) ──────────
export interface WaitingRequest {
  patientId: number
  scheduleId: number
}