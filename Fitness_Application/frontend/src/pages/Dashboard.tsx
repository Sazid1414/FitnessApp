import React, { useEffect, useState, Suspense } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { fitnessAPI, FitnessRecommendation, FitnessMetrics } from '../services/fitness';

interface FetchState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();

  const [metricsState, setMetricsState] = useState<FetchState<FitnessMetrics>>({ data: null, loading: true, error: null });
  const [recoState, setRecoState] = useState<FetchState<FitnessRecommendation>>({ data: null, loading: true, error: null });

  const hasProfile = Boolean(
    user?.height && user?.weight && user?.dateOfBirth && user?.gender && user?.activityLevel && user?.fitnessGoal
  );

  useEffect(() => {
    if (!hasProfile) {
  setMetricsState((s: FetchState<FitnessMetrics>) => ({ ...s, loading: false }));
  setRecoState((s: FetchState<FitnessRecommendation>) => ({ ...s, loading: false }));
      return;
    }
    // Fetch metrics
    (async () => {
      try {
        setMetricsState({ data: null, loading: true, error: null });
        const res = await fitnessAPI.getMetrics();
        setMetricsState({ data: res.data.data, loading: false, error: null });
      } catch (e: any) {
        setMetricsState({ data: null, loading: false, error: e.response?.data?.message || 'Failed to load metrics' });
      }
    })();
    // Fetch recommendations
    (async () => {
      try {
        setRecoState({ data: null, loading: true, error: null });
        const res = await fitnessAPI.getRecommendations();
        setRecoState({ data: res.data.data, loading: false, error: null });
      } catch (e: any) {
        setRecoState({ data: null, loading: false, error: e.response?.data?.message || 'Failed to load recommendations' });
      }
    })();
  }, [hasProfile]);

  const stats = [
    { name: 'BMR', value: metricsState.data ? Math.round(metricsState.data.bmr).toString() : '—', color: 'text-green-600' },
    { name: 'Daily Calories', value: metricsState.data ? Math.round(metricsState.data.dailyCalories).toString() : '—', color: 'text-blue-600' },
    { name: 'Target Calories', value: metricsState.data ? Math.round(metricsState.data.targetCalories).toString() : '—', color: 'text-purple-600' },
    { name: 'BMI', value: metricsState.data ? metricsState.data.bmi.toFixed(1) : '—', color: 'text-orange-600' },
  ];

  const ProfileForm = React.lazy(() => import('../components/ProfileForm'));

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
              <p className="text-gray-600">Welcome back, {user?.firstName}!</p>
            </div>
            <button
              onClick={logout}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition duration-200"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        {/* Stats */}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8">
          {stats.map((stat) => (
            <div key={stat.name} className="card">
              <div className="flex items-center">
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-600 truncate">{stat.name}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                </div>
                <div className={`text-sm font-medium ${stat.color}`}></div>
              </div>
            </div>
          ))}
        </div>

        {/* Fitness Recommendations & Quick Actions */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="card">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
            <div className="space-y-3">
              <button className="w-full btn-primary">
                + Add New Workout
              </button>
              <button className="w-full btn-secondary">
                📊 View Progress
              </button>
              <button className="w-full bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg transition duration-200">
                🤖 Get AI Recommendation
              </button>
            </div>
          </div>

          <div className="card">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Personalized Recommendation</h3>
            {!hasProfile && (
              <div className="space-y-4">
                <div className="text-sm text-gray-600">
                  Complete your profile (height, weight, DOB, gender, activity level, goal) to see recommendations.
                </div>
                <Suspense fallback={<div className="text-sm text-gray-500">Loading form...</div>}>
                  <ProfileForm onComplete={() => window.location.reload()} />
                </Suspense>
              </div>
            )}
            {hasProfile && (
              <div className="space-y-4">
                {recoState.loading && <p className="text-gray-500 text-sm">Loading recommendations...</p>}
                {recoState.error && <p className="text-red-600 text-sm">{recoState.error}</p>}
                {recoState.data && (
                  <>
                    <div className="grid grid-cols-2 gap-4 text-sm">
                      <div>
                        <p className="text-gray-600">BMI Category</p>
                        <p className="font-semibold">{recoState.data.bmiCategory}</p>
                      </div>
                      <div>
                        <p className="text-gray-600">Water Intake</p>
                        <p className="font-semibold">{Math.round(recoState.data.dailyWaterIntake)} ml</p>
                      </div>
                      <div className="col-span-2">
                        <p className="text-gray-600 mb-1">Macros (grams)</p>
                        <div className="flex gap-4 text-xs">
                          <span className="bg-blue-100 px-2 py-1 rounded">Protein: {Math.round(recoState.data.macroSplit.protein || 0)}g</span>
                          <span className="bg-green-100 px-2 py-1 rounded">Carbs: {Math.round(recoState.data.macroSplit.carbs || 0)}g</span>
                          <span className="bg-yellow-100 px-2 py-1 rounded">Fat: {Math.round(recoState.data.macroSplit.fat || 0)}g</span>
                        </div>
                      </div>
                    </div>
                    <div>
                      <p className="text-gray-600 mb-1 text-sm">Workout Suggestions</p>
                      <ul className="list-disc list-inside text-sm space-y-1">
                        {recoState.data.workoutRecommendations.slice(0,3).map((w, i) => (
                          <li key={i}>{w}</li>
                        ))}
                      </ul>
                    </div>
                  </>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Metrics Loading / Errors */}
        {metricsState.error && (
          <div className="card bg-red-50 border border-red-200 text-red-700 mb-8">
            Failed to load metrics: {metricsState.error}
          </div>
        )}
        {/* Placeholder for future recent activity component */}
      </main>
    </div>
  );
};

export default Dashboard;
