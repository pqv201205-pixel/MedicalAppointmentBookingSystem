import React from 'react'
import { Loader2 } from 'lucide-react'
import type { AppointmentStatus } from '@/types'
import { statusConfig } from '@/utils'

// ─── Button ───────────────────────────────────────────────────────────────────
interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  children: React.ReactNode
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary', size = 'md', loading, children, className = '', disabled, ...props
}) => {
  const variants = {
    primary:   'bg-blue-600 text-white hover:bg-blue-700 disabled:bg-blue-300',
    secondary: 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50',
    danger:    'bg-red-500 text-white hover:bg-red-600',
    ghost:     'text-gray-600 hover:bg-gray-100',
  }
  const sizes = { sm: 'px-3 py-1.5 text-sm', md: 'px-4 py-2 text-sm', lg: 'px-6 py-3' }

  return (
    <button
      {...props}
      disabled={disabled || loading}
      className={`inline-flex items-center justify-center gap-2 rounded-lg font-medium
        active:scale-95 transition-all duration-150 disabled:cursor-not-allowed
        ${variants[variant]} ${sizes[size]} ${className}`}
    >
      {loading && <Loader2 size={16} className="animate-spin" />}
      {children}
    </button>
  )
}

// ─── Input ────────────────────────────────────────────────────────────────────
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className = '', ...props }, ref) => (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1.5">{label}</label>
      )}
      <input
        ref={ref}
        {...props}
        className={`input ${error ? 'border-red-400 focus:ring-red-400' : ''} ${className}`}
      />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Input.displayName = 'Input'

// ─── Select ───────────────────────────────────────────────────────────────────
interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  options: { value: string | number; label: string }[]
  placeholder?: string
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, placeholder, className = '', ...props }, ref) => (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1.5">{label}</label>
      )}
      <select
        ref={ref}
        {...props}
        className={`input ${error ? 'border-red-400' : ''} ${className}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((o) => (
          <option key={o.value} value={o.value}>{o.label}</option>
        ))}
      </select>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Select.displayName = 'Select'

// ─── Textarea ─────────────────────────────────────────────────────────────────
interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  error?: string
}

export const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, error, className = '', ...props }, ref) => (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1.5">{label}</label>
      )}
      <textarea
        ref={ref}
        {...props}
        className={`input resize-none ${error ? 'border-red-400' : ''} ${className}`}
      />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  )
)
Textarea.displayName = 'Textarea'

// ─── Status Badge ─────────────────────────────────────────────────────────────
export const StatusBadge: React.FC<{ status: AppointmentStatus }> = ({ status }) => {
  const cfg = statusConfig[status]
  return <span className={cfg.className}>{cfg.label}</span>
}

// ─── Spinner ──────────────────────────────────────────────────────────────────
export const Spinner: React.FC<{ size?: number; className?: string }> = ({
  size = 24, className = '',
}) => (
  <Loader2 size={size} className={`animate-spin text-blue-600 ${className}`} />
)

// ─── Loading page ─────────────────────────────────────────────────────────────
export const PageLoader: React.FC = () => (
  <div className="min-h-screen flex items-center justify-center">
    <Spinner size={40} />
  </div>
)

// ─── Empty state ──────────────────────────────────────────────────────────────
export const EmptyState: React.FC<{ icon?: React.ReactNode; title: string; desc?: string }> = ({
  icon, title, desc,
}) => (
  <div className="flex flex-col items-center justify-center py-16 text-center">
    {icon && <div className="text-gray-300 mb-3">{icon}</div>}
    <p className="font-medium text-gray-500">{title}</p>
    {desc && <p className="text-sm text-gray-400 mt-1">{desc}</p>}
  </div>
)

// ─── Modal ────────────────────────────────────────────────────────────────────
interface ModalProps {
  open: boolean
  onClose: () => void
  title: string
  children: React.ReactNode
  footer?: React.ReactNode
  size?: 'sm' | 'md' | 'lg'
}

export const Modal: React.FC<ModalProps> = ({
  open, onClose, title, children, footer, size = 'md',
}) => {
  if (!open) return null
  const widths = { sm: 'max-w-sm', md: 'max-w-md', lg: 'max-w-2xl' }
  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
      onClick={onClose}
    >
      <div
        className={`card w-full ${widths[size]} shadow-xl`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h3 className="font-semibold text-gray-900">{title}</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            ✕
          </button>
        </div>
        <div className="px-6 py-4">{children}</div>
        {footer && (
          <div className="px-6 py-4 border-t border-gray-100 flex justify-end gap-3">
            {footer}
          </div>
        )}
      </div>
    </div>
  )
}
