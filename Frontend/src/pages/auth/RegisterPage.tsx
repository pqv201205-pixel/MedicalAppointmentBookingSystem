import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Stethoscope, Eye, EyeOff } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService } from '@/services'
import { Button, Input } from '@/components/common'

const schema = z.object({
  username: z.string().min(3, 'Tối thiểu 3 ký tự').max(50)
    .regex(/^[a-zA-Z0-9_]+$/, 'Chỉ chứa chữ, số và dấu _'),
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(6, 'Mật khẩu tối thiểu 6 ký tự'),
  confirmPassword: z.string(),
  phoneNumber: z.string().regex(/^(0|\+84)[0-9]{9}$/, 'Số điện thoại không hợp lệ'),
}).refine((d) => d.password === d.confirmPassword, {
  message: 'Mật khẩu xác nhận không khớp',
  path: ['confirmPassword'],
})
type FormData = z.infer<typeof schema>

const RegisterPage: React.FC = () => {
  const [showPw, setShowPw] = useState(false)
  const navigate = useNavigate()

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const onSubmit = async (data: FormData) => {
    try {
      await authService.register({
        username: data.username,
        email: data.email,
        password: data.password,
        phoneNumber: data.phoneNumber,
        role: 'PATIENT',
      })
      toast.success('Đăng ký thành công! Vui lòng đăng nhập.')
      navigate('/login')
    } catch (error: any){
    console.log("========== REGISTER ERROR ==========");
    console.log("Status:", error.response?.status);
    console.log("Data:", error.response?.data);
    console.log("Error:", error);
    console.log("====================================");

    toast.error(error.response?.data?.message || "Đăng ký thất bại");
  }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center p-4">
      <div className="card w-full max-w-md p-8 shadow-lg">
        <div className="flex flex-col items-center mb-6">
          <div className="w-12 h-12 bg-blue-600 rounded-xl flex items-center justify-center mb-2 shadow-md shadow-blue-200">
            <Stethoscope size={24} className="text-white" />
          </div>
          <h1 className="text-xl font-bold text-gray-900">Tạo tài khoản</h1>
          <p className="text-gray-500 text-sm">Đăng ký để đặt lịch khám bệnh</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            {...register('username')}
            label="Tên đăng nhập"
            placeholder="vd: nguyenvana"
            error={errors.username?.message}
          />
          <Input
            {...register('email')}
            type="email"
            label="Email"
            placeholder="vd: email@gmail.com"
            error={errors.email?.message}
          />
          <Input
            {...register('phoneNumber')}
            label="Số điện thoại"
            placeholder="vd: 0912345678"
            error={errors.phoneNumber?.message}
          />
          <div className="relative">
            <Input
              {...register('password')}
              type={showPw ? 'text' : 'password'}
              label="Mật khẩu"
              placeholder="Tối thiểu 6 ký tự"
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
          <Input
            {...register('confirmPassword')}
            type="password"
            label="Xác nhận mật khẩu"
            placeholder="Nhập lại mật khẩu"
            error={errors.confirmPassword?.message}
          />

          <Button type="submit" className="w-full mt-2" loading={isSubmitting} size="lg">
            Đăng ký
          </Button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-5">
          Đã có tài khoản?{' '}
          <Link to="/login" className="text-blue-600 font-medium hover:underline">
            Đăng nhập
          </Link>
        </p>
      </div>
    </div>
  )
}

export default RegisterPage
