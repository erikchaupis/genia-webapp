# Frontend
`cd frontend`
## Install
`npm install`
## Run
`npm run dev`

 - **URL**:  http://localhost:5173/

# Backend
`cd backend`
## Install
`npm install`
## Compiler TypeScript and Run
`npx tsc && node dist/index.js`

- **URL**:  http://localhost:3001
 - Frontend will call  http://localhost:3001/chat backend service


 # Troubleshooting
- npm libraries issues:
  - `npm i --save-dev @types/cors`
  - `npm install --save-dev @types/express`