import HomeTab from '../../tabs/HomeTab/HomeTab';
import ProfileTab from '../../tabs/ProfileTab/ProfileTab';
import RagTab from '../../tabs/RagTab/RagTab';

export const tabs = [
  { id: 'home', label: 'Chat', component: HomeTab },
  { id: 'rag', label: 'RAG', component: RagTab },
  { id: 'profile', label: 'Profile', component: ProfileTab }
];