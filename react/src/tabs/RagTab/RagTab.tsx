import React from 'react';
import Chat from "../../components/Chat/Chat";

const RagTab: React.FC = () => {
  return <Chat client="vertex" url="http://localhost:8080/api/chat/rag"/>;
};

export default RagTab;
