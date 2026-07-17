import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Stethoscope, Eye, EyeOff } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService } from '@/services'
import { useAuthStore } from '@/store/authStore'
import { Button, Input } from '@/components/common'

const schema = z.object({
  username: z.string().min(1, 'Vui lòng nhập tên đăng nhập'),
  password: z.string().min(1, 'Vui lòng nhập mật khẩu'),
})
type FormData = z.infer<typeof schema>

const LoginPage: React.FC = () => {
  const [showPw, setShowPw] = useState(false)
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const onSubmit = async (data: FormData) => {
    try {
      const res = await authService.login(data)
      const { user, accessToken, refreshToken } = res.data.data
      setAuth(user, accessToken, refreshToken)
      toast.success(`Chào mừng, ${user.username}!`)

      const redirect =
        user.role === 'ADMIN' ? '/admin/dashboard'
        : user.role === 'DOCTOR' ? '/doctor/dashboard'
        : '/patient/dashboard'
      navigate(redirect, { replace: true })
    } catch {}
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center p-4">
      <div className="card w-full max-w-md p-8 shadow-lg">
        {/* Logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 bg-blue-600 rounded-2xl flex items-center justify-center mb-3 shadow-md shadow-blue-200">
            <Stethoscope size={28} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900">MedBook</h1>
          <p className="text-gray-500 text-sm mt-1">Đặt lịch khám bệnh trực tuyến</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            {...register('username')}
            label="Tên đăng nhập"
            placeholder="Nhập tên đăng nhập"
            error={errors.username?.message}
          />

          <div className="relative">
            <Input
              {...register('password')}
              type={showPw ? 'text' : 'password'}
              label="Mật khẩu"
              placeholder="Nhập mật khẩu"
              error={errors.password?.message}
            />
            <button
              type="button"
              onClick={() => setShowPw(!showPw)}
              className="absolute right-3 top-8 text-gray-400 hover:text-gray-600"
            >
              {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>

          <div className="flex justify-end">
            <Link to="/forgot-password" className="text-sm text-blue-600 hover:underline">
              Quên mật khẩu?
            </Link>
          </div>

          <Button type="submit" className="w-full" loading={isSubmitting} size="lg">
            Đăng nhập
          </Button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-blue-600 font-medium hover:underline">
            Đăng ký ngay
          </Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage
