import React from 'react';
import './Menu.css';
import { Link } from 'react-router-dom';


const Menu = ({ isOpen, topBarHeight }) => {
  const menuStyle = {
    top: topBarHeight + 'px',
  };

  return (

    <>
      <div className={`Menu ${isOpen ? 'open' : ''}`} style={menuStyle}>
        <ul>

          <li>
            <Link className='btn' to="/">
              <i className="bi bi-house"></i>
              <span className="icon-text"> Home</span>
            </Link>
          </li>
        </ul>
      </div>
    </>
  );
}

export default Menu;
