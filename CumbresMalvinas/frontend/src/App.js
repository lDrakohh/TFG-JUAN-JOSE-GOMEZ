import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Login from "./auth/login";
import Logout from "./auth/logout";
import tokenService from "./services/token.service";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import SwaggerDocs from "./public/swagger";
import EmpresaListAdmin from "./admin/empresas/EmpresaListAdmin";
import EmpresaEditAdmin from "./admin/empresas/EmpresaEditAdmin";
import PrevisionList from "./public/previsiones/PrevisionList";
import PrevisionEdit from "./public/previsiones/PrevisionEdit";
import RegistroMercanciaForm from "./public/registros/RegistroMercancia";
import EnvaseListAdmin from "./admin/envases/EnvaseListAdmin";
import EnvaseEditAdmin from "./admin/envases/EnvaseEditAdmin";
import FrutaListAdmin from "./admin/frutas/FrutaListAdmin";
import FrutaEditAdmin from "./admin/frutas/FrutaEditAdmin";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let userRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/empresas" exact element={<PrivateRoute><EmpresaListAdmin /></PrivateRoute>} />
          <Route path="/empresas/:id" exact element={<PrivateRoute><EmpresaEditAdmin /></PrivateRoute>} />
          <Route path="/envases" exact element={<PrivateRoute><EnvaseListAdmin /></PrivateRoute>} />
          <Route path="/envases/:id" exact element={<PrivateRoute><EnvaseEditAdmin /></PrivateRoute>} />
          <Route path="/frutas" exact element={<PrivateRoute><FrutaListAdmin /></PrivateRoute>} />
          <Route path="/frutas/:id" exact element={<PrivateRoute><FrutaEditAdmin /></PrivateRoute>} />
        </>)
    }
  })
  if (!jwt) {
    publicRoutes = (
      <>
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="/previsiones" exact element={<PrivateRoute><PrevisionList /></PrivateRoute>} />
        <Route path="/previsiones/:id" exact element={<PrivateRoute><PrevisionEdit /></PrivateRoute>} />
        <Route path="/registros" exact element={<PrivateRoute><RegistroMercanciaForm /></PrivateRoute>} />
        <Route path="/registros/:id" exact element={<PrivateRoute><RegistroMercanciaForm /></PrivateRoute>} />
      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          <Route path="/docs" element={<SwaggerDocs />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
