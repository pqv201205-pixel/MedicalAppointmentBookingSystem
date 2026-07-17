import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Stethoscope, ArrowLeft } from 'lucide-react'
import toast from 'react-hot-toast'
import { authService } from '@/services'
import { Button, Input } from '@/components/common'

const emailSchema = z.object({
  email: z.string().email('Email không hợp lệ'),
})
type EmailForm = z.infer<typeof emailSchema>

const resetSchema = z.object({
  otp:             z.string().length(6, 'OTP gồm đúng 6 chữ số'),
  newPassword:     z.string().min(6, 'Mật khẩu tối thiểu 6 ký tự'),
  confirmPassword: z.string(),
}).refine((d) => d.newPassword === d.confirmPassword, {
  message: 'Mật khẩu xác nhận không khớp',
  path: ['confirmPassword'],
})
type ResetForm = z.infer<typeof resetSchema>

const ForgotPasswordPage: React.FC = () => {
  const [step, setStep] = useState<1 | 2>(1)
  const [email, setEmail] = useState('')
  const navigate = useNavigate()

  const emailForm = useForm<EmailForm>({ resolver: zodResolver(emailSchema) })
  const resetForm = useForm<ResetForm>({ resolver: zodResolver(resetSchema) })

  const onSendOtp = async (data: EmailForm) => {
    try {
      await authService.forgotPassword(data.email)
      setEmail(data.email)
      toast.success('Đã gửi mã OTP về email của bạn')
      setStep(2)
    } catch {}
  }

  const onResetPassword = async (data: ResetForm) => {
    try {
      await authService.resetPassword({
        email,
        otp: data.otp,
        newPassword: data.newPassword,
      })
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập.')
      navigate('/login')
    } catch {}
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 flex items-center justify-center p-4">
      <div className="card w-full max-w-md p-8 shadow-lg">
        <div className="flex flex-col items-center mb-6">
          <div className="w-12 h-12 bg-blue-600 rounded-xl flex items-center justify-center mb-2 shadow-md shadow-blue-200">
            <Stethoscope size={24} className="text-white" />
          </div>
          <h1 className="text-xl font-bold text-gray-900">Quên mật khẩu</h1>
          <p className="text-gray-500 text-sm text-center mt-1">
            {step === 1
              ? 'Nhập email để nhận mã OTP xác nhận'
              : `Mã OTP đã gửi tới ${email}`}
          </p>
        </div>

        {/* Step indicator */}
        <div className="flex items-center gap-2 mb-6">
          {[1, 2].map((s) => (
            <React.Fragment key={s}>
              <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-colors
                ${step >= s ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-400'}`}>
                {s}
              </div>
              {s < 2 && <div className={`flex-1 h-0.5 ${step > s ? 'bg-blue-600' : 'bg-gray-100'}`} />}
            </React.Fragment>
          ))}
        </div>

        {step === 1 ? (
          <form onSubmit={emailForm.handleSubmit(onSendOtp)} className="space-y-4">
            <Input
              {...emailForm.register('email')}
              type="email"
              label="Email đã đăng ký"
              placeholder="vd: email@gmail.com"
              error={emailForm.formState.errors.email?.message}
            />
            <Button type="submit" className="w-full" size="lg"
              loading={emailForm.formState.isSubmitting}>
              Gửi mã OTP
            </Button>
          </form>
        ) : (
          <form onSubmit={resetForm.handleSubmit(onResetPassword)} className="space-y-4">
            <div>
              <Input
                {...resetForm.register('otp')}
                label="Mã OTP (6 số)"
                placeholder="Nhập mã từ email"
                maxLength={6}
                error={resetForm.formState.errors.otp?.message}
              />
              <button
                type="button"
                onClick={() => onSendOtp({ email })}
                className="text-xs text-blue-600 hover:underline mt-1"
              >
                Gửi lại mã
              </button>
            </div>
            <Input
              {...resetForm.register('newPassword')}
              type="password"
              label="Mật khẩu mới"
              placeholder="Tối thiểu 6 ký tự"
              error={resetForm.formState.errors.newPassword?.message}
            />
            <Input
              {...resetForm.register('confirmPassword')}
              type="password"
              label="Xác nhận mật khẩu mới"
              placeholder="Nhập lại mật khẩu mới"
              error={resetForm.formState.errors.confirmPassword?.message}
            />
            <Button type="submit" className="w-full" size="lg"
              loading={resetForm.formState.isSubmitting}>
              Đặt lại mật khẩu
            </Button>
          </form>
        )}

        <p className="text-center mt-5">
          <Link to="/login" className="text-sm text-blue-600 hover:underline flex items-center justify-center gap-1">
            <ArrowLeft size={13} /> Quay lại đăng nhập
          </Link>
        </p>
      </div>
    </div>
  )
}

export default ForgotPasswordPage
