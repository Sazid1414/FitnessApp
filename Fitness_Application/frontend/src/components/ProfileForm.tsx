import React, { useState } from 'react';
import { updateProfile } from '../services/api';

interface ProfileFormProps {
  onComplete?: () => void;
}

const ProfileForm: React.FC<ProfileFormProps> = ({ onComplete }) => {
  const [form, setForm] = useState({
    gender: 'MALE',
    height: '',
    weight: '',
    activityLevel: 'MODERATELY_ACTIVE',
    fitnessGoal: 'MAINTAIN_WEIGHT'
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await updateProfile({
        gender: form.gender,
        height: Number(form.height),
        weight: Number(form.weight),
        activityLevel: form.activityLevel,
        fitnessGoal: form.fitnessGoal
      } as any); // TODO: tighten type with shared DTO
      onComplete?.();
    } catch (err: any) {
      setError(err.message || 'Failed to update profile');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 p-4 border rounded bg-white shadow">
      <h2 className="text-lg font-semibold">Complete Your Profile</h2>
      {error && <div className="text-red-600 text-sm">{error}</div>}
      <div>
        <label className="block text-sm font-medium">Gender</label>
        <select name="gender" value={form.gender} onChange={handleChange} className="mt-1 w-full border p-2 rounded">
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
          <option value="OTHER">Other</option>
        </select>
      </div>
      <div className="flex gap-4">
        <div className="flex-1">
          <label className="block text-sm font-medium">Height (cm)</label>
            <input name="height" type="number" value={form.height} onChange={handleChange} required className="mt-1 w-full border p-2 rounded" />
        </div>
        <div className="flex-1">
          <label className="block text-sm font-medium">Weight (kg)</label>
            <input name="weight" type="number" value={form.weight} onChange={handleChange} required className="mt-1 w-full border p-2 rounded" />
        </div>
      </div>
      <div>
        <label className="block text-sm font-medium">Activity Level</label>
        <select name="activityLevel" value={form.activityLevel} onChange={handleChange} className="mt-1 w-full border p-2 rounded">
          <option value="SEDENTARY">Sedentary</option>
          <option value="LIGHTLY_ACTIVE">Lightly Active</option>
          <option value="MODERATELY_ACTIVE">Moderately Active</option>
          <option value="VERY_ACTIVE">Very Active</option>
          <option value="EXTRA_ACTIVE">Extra Active</option>
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium">Fitness Goal</label>
        <select name="fitnessGoal" value={form.fitnessGoal} onChange={handleChange} className="mt-1 w-full border p-2 rounded">
          <option value="LOSE_WEIGHT">Lose Weight</option>
          <option value="MAINTAIN_WEIGHT">Maintain Weight</option>
          <option value="GAIN_MUSCLE">Gain Muscle</option>
          <option value="IMPROVE_ENDURANCE">Improve Endurance</option>
        </select>
      </div>
      <button type="submit" disabled={submitting} className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50">
        {submitting ? 'Saving...' : 'Save Profile'}
      </button>
    </form>
  );
};

export default ProfileForm;
