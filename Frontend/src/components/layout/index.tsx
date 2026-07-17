import React, { useState } from 'react'
import { NavLink, useNavigate, Outlet } from 'react-router-dom'
import {
  LayoutDashboard, Calendar, User, Users, Stethoscope,
  Bell, LogOut, Menu, X, ChevronRight, ClipboardList,
} from 'lucide-react'
import { useAuthStore } from '@/store/authStore'
import { authService } from '@/services'
import toast from 'react-hot-toast'

// ─── Nav config theo role ─────────────────────────────────────────────────────
const navByRole = {
  PATIENT: [
    { to: '/patient/dashboard', icon: LayoutDashboard, label: 'Tổng quan' },
    { to: '/patient/doctors',   icon: Stethoscope,     label: 'Tìm bác sĩ' },
    { to: '/patient/appointments', icon: Calendar,      label: 'Lịch hẹn' },
    { to: '/patient/profile',   icon: User,             label: 'Hồ sơ' },
  ],
  DOCTOR: [
    { to: '/doctor/dashboard',  icon: LayoutDashboard,  label: 'Tổng quan' },
    { to: '/doctor/appointments', icon: Calendar,        label: 'Lịch hẹn' },
    { to: '/doctor/schedules',  icon: ClipboardList,     label: 'Lịch làm việc' },
    { to: '/doctor/profile',    icon: User,              label: 'Hồ sơ' },
  ],
  ADMIN: [
    { to: '/admin/dashboard',   icon: LayoutDashboard,  label: 'Tổng quan' },
    { to: '/admin/appointments', icon: Calendar,         label: 'Lịch hẹn' },
    { to: '/admin/doctors',     icon: Stethoscope,       label: 'Bác sĩ' },
    { to: '/admin/patients',    icon: Users,             label: 'Bệnh nhân' },
    { to: '/admin/specialties', icon: ClipboardList,     label: 'Chuyên khoa' },
  ],
}

// ─── Sidebar ──────────────────────────────────────────────────────────────────
const Sidebar: React.FC<{ open: boolean; onClose: () => void }> = ({ open, onClose }) => {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const navItems = user ? navByRole[user.role] ?? [] : []

  const handleLogout = async () => {
    try { await authService.logout() } catch {}
    logout()
    toast.success('Đã đăng xuất')
    navigate('/login')
  }

  return (
    <>
      {/* Overlay mobile */}
      {open && (
        <div className="fixed inset-0 z-20 bg-black/40 lg:hidden" onClick={onClose} />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-30 w-64 bg-white border-r border-gray-100
          flex flex-col transform transition-transform duration-200
          ${open ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0`}
      >
        {/* Logo */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-gray-100">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <Stethoscope size={18} className="text-white" />
            </div>
            <span className="font-bold text-gray-900">MedBook</span>
          </div>
          <button onClick={onClose} className="lg:hidden text-gray-400 hover:text-gray-600">
            <X size={20} />
          </button>
        </div>

        {/* User info */}
        <div className="px-4 py-3 mx-3 mt-3 bg-blue-50 rounded-xl">
          <div className="w-9 h-9 rounded-full bg-blue-600 flex items-center justify-center text-white font-semibold text-sm mb-2">
            {user?.username?.[0]?.toUpperCase()}
          </div>
          <p className="font-medium text-gray-900 text-sm truncate">{user?.username}</p>
          <p className="text-xs text-blue-600 font-medium">
            {user?.role === 'ADMIN' ? 'Quản trị viên' : user?.role === 'DOCTOR' ? 'Bác sĩ' : 'Bệnh nhân'}
          </p>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all
                ${isActive
                  ? 'bg-blue-600 text-white shadow-sm shadow-blue-200'
                  : 'text-gray-600 hover:bg-gray-100'}`
              }
            >
              {({ isActive }) => (
                <>
                  <Icon size={18} />
                  <span className="flex-1">{label}</span>
                  {isActive && <ChevronRight size={14} className="opacity-70" />}
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* Logout */}
        <div className="px-3 pb-4">
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm
              font-medium text-red-500 hover:bg-red-50 transition-all"
          >
            <LogOut size={18} />
            Đăng xuất
          </button>
        </div>
      </aside>
    </>
  )
}

// ─── Header ───────────────────────────────────────────────────────────────────
const Header: React.FC<{ onMenuClick: () => void }> = ({ onMenuClick }) => {
  const { user } = useAuthStore()
  const navigate = useNavigate()

  return (
    <header className="sticky top-0 z-10 bg-white border-b border-gray-100 px-4 py-3">
      <div className="flex items-center justify-between max-w-screen-xl mx-auto">
        <button
          onClick={onMenuClick}
          className="lg:hidden p-2 rounded-lg text-gray-500 hover:bg-gray-100"
        >
          <Menu size={20} />
        </button>

        <div className="flex-1 lg:flex-none" />

        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate(
              user?.role === 'PATIENT' ? '/patient/dashboard'
              : user?.role === 'DOCTOR' ? '/doctor/dashboard'
              : '/admin/dashboard'
            )}
            className="relative p-2 rounded-lg text-gray-500 hover:bg-gray-100 transition-colors"
          >
            <Bell size={20} />
          </button>

          <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center
            text-white font-semibold text-sm">
            {user?.username?.[0]?.toUpperCase()}
          </div>
        </div>
      </div>
    </header>
  )
}

// ─── Main Layout ──────────────────────────────────────────────────────────────
export const MainLayout: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="flex-1 lg:ml-64 flex flex-col min-w-0">
        <Header onMenuClick={() => setSidebarOpen(true)} />
        <main className="flex-1 p-4 md:p-6">
          <div className="max-w-screen-xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}

// ─── Protected Route ──────────────────────────────────────────────────────────
import { Navigate } from 'react-router-dom'

interface ProtectedRouteProps {
  allowedRoles?: ('ADMIN' | 'DOCTOR' | 'PATIENT')[]
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const { isAuthenticated, user } = useAuthStore()

  if (!isAuthenticated) return <Navigate to="/login" replace />

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    const redirectTo =
      user.role === 'ADMIN' ? '/admin/dashboard'
      : user.role === 'DOCTOR' ? '/doctor/dashboard'
      : '/patient/dashboard'
    return <Navigate to={redirectTo} replace />
  }

  return <Outlet />
}
