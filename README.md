# Frontend
`cd frontend`
## Install
`npm install`
## Run
`npm run dev`

- **URL**:  http://localhost:3001

# Backend
`cd backend`
## Install
`npm install`
## Compiler TypeScript and Run
`npx tsc && node dist/index.js`

 - **URL**:  http://localhost:5173/
 - It will call  http://localhost:3001/chat service from backend


 # Troubleshooting
- npm libraries issues:
  - `npm i --save-dev @types/cors`
  - `npm install --save-dev @types/express`