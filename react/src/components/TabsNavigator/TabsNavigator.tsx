import { NavLink, Outlet } from 'react-router-dom';
import { tabs } from './tabs';

const TabsNavigator: React.FC = () => {
  return (
    <>
      <h1>Gen AI Application</h1>
      <ul className="nav nav-tabs">
        {tabs.map((tab) => (
          <li className="nav-item" key={tab.id}>
            <NavLink
              to={`/${tab.id}`}
              className={({ isActive }) =>
                'nav-link' + (isActive ? ' active' : '')
              }
              role="tab"
            >
              {tab.label}
            </NavLink>
          </li>
        ))}
      </ul>

      <div className="tab-content mt-3">
        <Outlet />
      </div>
    </>
  );
};

export default TabsNavigator;
