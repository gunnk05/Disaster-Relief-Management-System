import React from 'react';
import '../styles/Navbar.css';

const Navbar = ({ title }) => {
  return (
    <nav className="navbar">
      <h1>{title || 'DRRMS'}</h1>
    </nav>
  );
};

export default Navbar;
