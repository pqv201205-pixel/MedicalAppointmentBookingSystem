import React, { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { patientService } from '@/services'
import { Button, Input, Spinner } from '@/components/common'
import toast from 'react-hot-toast'

const schema = z.object({
  fullName:      z.string().min(1, 'Không được để trống'),
  gender:        z.enum(['MALE', 'FEMALE', 'OTHER']),
  dateOfBirth:   z.string().min(1, 'Vui lòng chọn ngày sinh'),
  address:       z.string().optional(),
  medicalHistory:z.string().optional(),
})
type FormData = z.infer<typeof schema>

const PatientProfilePage: React.FC = () => {
  const { register, handleSubmit, reset, formState: { errors, isSubmitting, isDirty } } =
    useForm<FormData>({ resolver: zodResolver(schema) })

  useEffect(() => {
    patientService.getMyProfile().then((r) => {
      const p = r.data.data
      reset({
        fullName:       p.fullName,
        gender:         p.gender as any,
        dateOfBirth:    p.dateOfBirth,
        address:        p.address,
        medicalHistory: p.medicalHistory,
      })
    }).catch(() => {})
  }, [reset])

  const onSubmit = async (data: FormData) => {
    try {
      await patientService.updateMyProfile(data)
      reset(data)
      toast.success('Cập nhật hồ sơ thành công')
    } catch {}
  }

  return (
    <div className="max-w-2xl space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Hồ sơ của tôi</h1>
      <div className="card p-6">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input {...register('fullName')} label="Họ và tên" error={errors.fullName?.message} />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Giới tính</label>
            <select {...register('gender')} className="input">
              <option value="MALE">Nam</option>
              <option value="FEMALE">Nữ</option>
              <option value="OTHER">Khác</option>
            </select>
          </div>
          <Input {...register('dateOfBirth')} type="date" label="Ngày sinh" error={errors.dateOfBirth?.message} />
          <Input {...register('address')} label="Địa chỉ" placeholder="Số nhà, phường/xã, quận/huyện, tỉnh/thành" />
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">Tiền sử bệnh</label>
            <textarea
              {...register('medicalHistory')}
              rows={4}
              className="input resize-none"
              placeholder="Các bệnh đã mắc, dị ứng thuốc..."
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

export default PatientProfilePage
