import React, { useEffect, useState } from 'react'
import { Search } from 'lucide-react'
import { doctorService } from '@/services'
import type { Doctor } from '@/types'
import { EmptyState, Spinner, StatusBadge } from '@/components/common'
import { formatCurrency } from '@/utils'

const AdminDoctorsPage: React.FC = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    doctorService.getDoctors()
      .then((r) => setDoctors(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const filtered = doctors.filter((d) =>
    d.fullName.toLowerCase().includes(search.toLowerCase()) ||
    d.specialty.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Quản lý bác sĩ</h1>
        <p className="text-gray-500 mt-1">{doctors.length} bác sĩ trong hệ thống</p>
      </div>

      <div className="relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm theo tên, chuyên khoa..."
          className="input pl-9 max-w-sm"
        />
      </div>

      {loading ? (
        <div className="flex justify-center py-16"><Spinner /></div>
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr>
                  {['#', 'Họ tên', 'Chuyên khoa', 'Học vị', 'Kinh nghiệm', 'Phí tư vấn', 'Email'].map((h) => (
                    <th key={h} className="text-left py-3 px-4 font-medium text-gray-500 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.length === 0 ? (
                  <tr><td colSpan={7} className="text-center py-12 text-gray-400">Không có dữ liệu</td></tr>
                ) : filtered.map((d, i) => (
                  <tr key={d.doctorId} className="border-b border-gray-50 hover:bg-gray-50">
                    <td className="py-3 px-4 text-gray-400">{i + 1}</td>
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-bold text-sm flex-shrink-0">
                          {d.fullName[0]}
                        </div>
                        <span className="font-medium text-gray-900 whitespace-nowrap">BS. {d.fullName}</span>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-blue-600 whitespace-nowrap">{d.specialty}</td>
                    <td className="py-3 px-4 text-gray-600">{d.degree || '—'}</td>
                    <td className="py-3 px-4 text-gray-600">{d.experienceYears} năm</td>
                    <td className="py-3 px-4 text-gray-900 whitespace-nowrap">{formatCurrency(d.consultationFee)}</td>
                    <td className="py-3 px-4 text-gray-500">{d.user?.email}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminDoctorsPage
