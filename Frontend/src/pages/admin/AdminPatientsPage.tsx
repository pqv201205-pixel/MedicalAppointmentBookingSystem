import React, { useEffect, useState } from 'react'
import { Search } from 'lucide-react'
import { patientService } from '@/services'
import type { Patient } from '@/types'
import { Spinner } from '@/components/common'
import { formatDate } from '@/utils'

const AdminPatientsPage: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    // Dùng admin endpoint lấy tất cả
    patientService.getAll()
      .then((r) => setPatients(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const filtered = patients.filter((p) =>
    p.fullName.toLowerCase().includes(search.toLowerCase()) ||
    p.user?.email?.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Quản lý bệnh nhân</h1>
        <p className="text-gray-500 mt-1">{patients.length} bệnh nhân trong hệ thống</p>
      </div>

      <div className="relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Tìm theo tên, email..."
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
                  {['#', 'Họ tên', 'Giới tính', 'Ngày sinh', 'Địa chỉ', 'Email', 'SĐT'].map((h) => (
                    <th key={h} className="text-left py-3 px-4 font-medium text-gray-500 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.length === 0 ? (
                  <tr><td colSpan={7} className="text-center py-12 text-gray-400">Không có dữ liệu</td></tr>
                ) : filtered.map((p, i) => (
                  <tr key={p.patientId} className="border-b border-gray-50 hover:bg-gray-50">
                    <td className="py-3 px-4 text-gray-400">{i + 1}</td>
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center text-green-700 font-bold text-sm flex-shrink-0">
                          {p.fullName[0]}
                        </div>
                        <span className="font-medium text-gray-900 whitespace-nowrap">{p.fullName}</span>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-gray-600">
                      {{ MALE: 'Nam', FEMALE: 'Nữ', OTHER: 'Khác' }[p.gender] ?? p.gender}
                    </td>
                    <td className="py-3 px-4 text-gray-600 whitespace-nowrap">
                      {p.dateOfBirth ? formatDate(p.dateOfBirth) : '—'}
                    </td>
                    <td className="py-3 px-4 text-gray-600 max-w-[200px] truncate">{p.address || '—'}</td>
                    <td className="py-3 px-4 text-gray-500">{p.user?.email}</td>
                    <td className="py-3 px-4 text-gray-500">{p.user?.phoneNumber || '—'}</td>
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

export default AdminPatientsPage
