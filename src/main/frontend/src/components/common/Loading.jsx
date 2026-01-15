export const Spinner = ({ size = 'md', className = '' }) => {
    const sizes = {
        sm: 'w-4 h-4',
        md: 'w-8 h-8',
        lg: 'w-12 h-12',
    };

    return (
        <div className={`inline-block animate-spin rounded-full border-4 border-solid border-current border-r-transparent ${sizes[size]} ${className}`} role="status">
            <span className="sr-only">Loading...</span>
        </div>
    );
};

export const LoadingOverlay = ({ message = 'Loading...' }) => {
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 flex flex-col items-center gap-4">
                <Spinner size="lg" className="text-primary-600" />
                <p className="text-gray-700 font-medium">{message}</p>
            </div>
        </div>
    );
};

export const PageLoader = () => {
    return (
        <div className="flex items-center justify-center min-h-screen">
            <Spinner size="lg" className="text-primary-600" />
        </div>
    );
};
