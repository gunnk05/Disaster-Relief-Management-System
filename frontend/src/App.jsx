import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';

import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import UserDashboard from './pages/UserDashboard';
import VolunteerDashboard from './pages/VolunteerDashboard';
import ProfileSettings from './pages/ProfileSettings';
import CreateRequest from './pages/CreateRequest';
import FindResources from './pages/FindResources';
import MyRequests from './pages/MyRequests';
import Shelters from './pages/Shelters';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/admin" element={<ProtectedRoute allowedRoles={['admin']}><AdminDashboard /></ProtectedRoute>} />
      <Route path="/volunteer" element={<ProtectedRoute allowedRoles={['volunteer']}><VolunteerDashboard /></ProtectedRoute>} />
      <Route path="/citizen" element={<ProtectedRoute allowedRoles={['citizen']}><UserDashboard /></ProtectedRoute>} />
      <Route path="/create-request" element={<ProtectedRoute allowedRoles={['citizen']}><CreateRequest /></ProtectedRoute>} />
      <Route path="/find-resources" element={<ProtectedRoute allowedRoles={['citizen']}><FindResources /></ProtectedRoute>} />
      <Route path="/my-requests" element={<ProtectedRoute allowedRoles={['citizen']}><MyRequests /></ProtectedRoute>} />
      <Route path="/shelters" element={<ProtectedRoute allowedRoles={['citizen']}><Shelters /></ProtectedRoute>} />
      <Route path="/profiles" element={<ProtectedRoute allowedRoles={['citizen', 'volunteer', 'admin']}><ProfileSettings /></ProtectedRoute>} />
    </Routes>
  );
}

export default App;
