import React from 'react';
import Chat from "../../components/Chat/Chat";

const HomeTab: React.FC = () => {
  // client: {vertex , openai, gemini}
  return <Chat client="vertex" url="http://localhost:8080/api/chat"/>;
};

export default HomeTab;
