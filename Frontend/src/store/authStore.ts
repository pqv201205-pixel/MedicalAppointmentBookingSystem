import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { User } from '@/types'

interface AuthState {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean

  setAuth: (user: User, accessToken: string, refreshToken: string) => void
  logout: () => void
  updateUser: (user: Partial<User>) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,

      setAuth: (user, accessToken, refreshToken) => {
        // Lưu token vào localStorage để Axios interceptor dùng
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)
        set({ user, accessToken, refreshToken, isAuthenticated: true })
      },

      logout: () => {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
        })
      },

      updateUser: (updated) =>
        set((s) => ({
          user: s.user ? { ...s.user, ...updated } : null,
        })),
    }),
    {
      name: 'auth-store',
      // Chỉ persist user và isAuthenticated, không persist token (dùng localStorage trực tiếp)
      partialize: (s) => ({
        user: s.user,
        isAuthenticated: s.isAuthenticated,
      }),
    }
  )
)
