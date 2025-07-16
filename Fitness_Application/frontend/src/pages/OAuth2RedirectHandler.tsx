import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const OAuth2RedirectHandler: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { loginWithToken } = useAuth();

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const token = urlParams.get('token');
    const email = urlParams.get('email');
    const name = urlParams.get('name');
    const error = urlParams.get('error');

    if (token && email) {
      // Update auth context with token and user info
      loginWithToken(token, { 
        id: Date.now(), // Temporary ID for OAuth2 users
        email, 
        firstName: name?.split(' ')[0] || email,
        lastName: name?.split(' ')[1] || '',
        role: 'USER',
        enabled: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      });
      
      // Redirect to dashboard
      navigate('/dashboard', { replace: true });
    } else if (error) {
      console.error('OAuth2 login error:', error);
      navigate('/login?error=oauth2_failed', { replace: true });
    } else {
      console.error('OAuth2 login failed: No token received');
      navigate('/login?error=oauth2_failed', { replace: true });
    }
  }, [location, navigate, loginWithToken]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <h2 className="mt-6 text-xl font-semibold text-gray-900">
            Processing login...
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Please wait while we complete your authentication.
          </p>
        </div>
      </div>
    </div>
  );
};

export default OAuth2RedirectHandler;
