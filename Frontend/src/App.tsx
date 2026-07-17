import React, { Suspense, lazy } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { MainLayout, ProtectedRoute } from '@/components/layout'
import { PageLoader } from '@/components/common'

// ── Auth (lazy load) ──────────────────────────────────────────────────────────
const LoginPage         = lazy(() => import('@/pages/auth/LoginPage'))
const RegisterPage      = lazy(() => import('@/pages/auth/RegisterPage'))
const ForgotPasswordPage = lazy(() => import('@/pages/auth/ForgotPasswordPage'))

// ── Patient (lazy load) ───────────────────────────────────────────────────────
const PatientDashboard       = lazy(() => import('@/pages/patient/PatientDashboard'))
const DoctorsListPage        = lazy(() => import('@/pages/patient/DoctorsListPage'))
const BookAppointmentPage    = lazy(() => import('@/pages/patient/BookAppointmentPage'))
const PatientAppointmentsPage = lazy(() => import('@/pages/patient/PatientAppointmentsPage'))
const PatientProfilePage     = lazy(() => import('@/pages/patient/PatientProfilePage'))

// ── Doctor (lazy load) ────────────────────────────────────────────────────────
const DoctorDashboard       = lazy(() => import('@/pages/doctor/DoctorDashboard'))
const DoctorAppointmentsPage = lazy(() => import('@/pages/doctor/DoctorAppointmentsPage'))
const DoctorSchedulesPage   = lazy(() => import('@/pages/doctor/DoctorSchedulesPage'))
const DoctorProfilePage     = lazy(() => import('@/pages/doctor/DoctorProfilePage'))

// ── Admin (lazy load) ─────────────────────────────────────────────────────────
const AdminDashboard       = lazy(() => import('@/pages/admin/AdminDashboard'))
const AdminAppointmentsPage = lazy(() => import('@/pages/admin/AdminAppointmentsPage'))
const AdminSpecialtiesPage = lazy(() => import('@/pages/admin/AdminSpecialtiesPage'))
const AdminDoctorsPage     = lazy(() => import('@/pages/admin/AdminDoctorsPage'))
const AdminPatientsPage    = lazy(() => import('@/pages/admin/AdminPatientsPage'))

const App: React.FC = () => (
  <BrowserRouter>
    <Toaster
      position="top-right"
      toastOptions={{
        duration: 3000,
        style: { fontSize: '14px' },
      }}
    />
    <Suspense fallback={<PageLoader />}>
      <Routes>
        {/* ── Public ─────────────────────────────────────────────────────── */}
        <Route path="/"                element={<Navigate to="/login" replace />} />
        <Route path="/login"           element={<LoginPage />} />
        <Route path="/register"        element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />

        {/* ── Patient ────────────────────────────────────────────────────── */}
        <Route element={<ProtectedRoute allowedRoles={['PATIENT']} />}>
          <Route element={<MainLayout />}>
            <Route path="/patient/dashboard"        element={<PatientDashboard />} />
            <Route path="/patient/doctors"          element={<DoctorsListPage />} />
            <Route path="/patient/book/:doctorId"   element={<BookAppointmentPage />} />
            <Route path="/patient/appointments"     element={<PatientAppointmentsPage />} />
            <Route path="/patient/profile"          element={<PatientProfilePage />} />
          </Route>
        </Route>

        {/* ── Doctor ─────────────────────────────────────────────────────── */}
        <Route element={<ProtectedRoute allowedRoles={['DOCTOR']} />}>
          <Route element={<MainLayout />}>
            <Route path="/doctor/dashboard"    element={<DoctorDashboard />} />
            <Route path="/doctor/appointments" element={<DoctorAppointmentsPage />} />
            <Route path="/doctor/schedules"    element={<DoctorSchedulesPage />} />
            <Route path="/doctor/profile"      element={<DoctorProfilePage />} />
          </Route>
        </Route>

        {/* ── Admin ──────────────────────────────────────────────────────── */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route element={<MainLayout />}>
            <Route path="/admin/dashboard"    element={<AdminDashboard />} />
            <Route path="/admin/appointments" element={<AdminAppointmentsPage />} />
            <Route path="/admin/specialties"  element={<AdminSpecialtiesPage />} />
            <Route path="/admin/doctors"      element={<AdminDoctorsPage />} />
            <Route path="/admin/patients"     element={<AdminPatientsPage />} />
          </Route>
        </Route>

        {/* ── Fallback ───────────────────────────────────────────────────── */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Suspense>
  </BrowserRouter>
)

export default App
