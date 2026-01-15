export const Card = ({ children, title, className = '', headerAction }) => {
    return (
        <div className={`bg-white rounded-[2.5rem] border border-slate-100 shadow-[0_10px_40px_-15px_rgba(0,0,0,0.03)] overflow-hidden ${className}`}>
            {(title || headerAction) && (
                <div className="px-8 py-6 border-b border-slate-50 flex items-center justify-between bg-slate-50/30">
                    {title && (
                        <h3 className="text-xl font-black text-slate-900 font-heading tracking-tight">
                            {title}
                        </h3>
                    )}
                    {headerAction && <div className="flex-shrink-0">{headerAction}</div>}
                </div>
            )}
            <div className="p-8">
                {children}
            </div>
        </div>
    );
};
