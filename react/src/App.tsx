import { Routes, Route, Navigate } from 'react-router-dom';
import TabsNavigator from './components/TabsNavigator/TabsNavigator';
import { tabs } from './components/TabsNavigator/tabs';

const App: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<TabsNavigator />}>
        {tabs.map(({ id, component: Component }) => (
          <Route
            key={id}
            path={id}
            element={<Component />}
          />
        ))}
        <Route path="*" element={<Navigate to="/home" replace />} />
      </Route>
    </Routes>
  );
};

export default App;