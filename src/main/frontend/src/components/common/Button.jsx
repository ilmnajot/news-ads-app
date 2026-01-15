export const Button = ({
    children,
    variant = 'primary',
    size = 'md',
    className = '',
    disabled = false,
    ...props
}) => {
    const baseStyles = 'inline-flex items-center justify-center font-bold transition-all duration-300 transform active:scale-[0.97] disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none select-none';

    const variants = {
        primary: 'bg-primary-600 text-white shadow-lg shadow-primary-500/25 hover:bg-primary-700 hover:shadow-primary-500/40 rounded-2xl',
        secondary: 'bg-slate-50 text-slate-700 border border-slate-200 hover:bg-slate-100 hover:border-slate-300 rounded-2xl',
        danger: 'bg-rose-50 text-rose-600 hover:bg-rose-100 border border-rose-100 rounded-2xl',
        success: 'bg-emerald-50 text-emerald-600 hover:bg-emerald-100 border border-emerald-100 rounded-2xl',
        outline: 'bg-transparent border-2 border-slate-200 text-slate-600 hover:border-primary-500 hover:text-primary-600 rounded-2xl',
        glass: 'glass text-slate-700 hover:bg-white/50 rounded-2xl border-white/40',
    };

    const sizes = {
        sm: 'px-4 py-2 text-xs gap-1.5',
        md: 'px-6 py-3.5 text-sm gap-2',
        lg: 'px-8 py-4 text-base gap-3',
    };

    return (
        <button
            className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
            disabled={disabled}
            {...props}
        >
            {children}
        </button>
    );
};
