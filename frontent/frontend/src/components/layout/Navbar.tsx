import { useEffect, useState } from "react";
import "./Havbar.css";

type StoreUser = {
    id: number;
    username: string;
    email: string;
};

function readUser(): StoreUser | null {
  try {
    const raw = localStorage.getItem("user");
    if (!raw) return null;
    return JSON.parse(raw) as StoreUser;
  } catch {
    return null;
  }
}

export default function Navbar(){
    const [user, setUser] = useState<StoreUser | null>(() => readUser());

  useEffect(() => {
    const sync = () => setUser(readUser());

    window.addEventListener("user-updated", sync);

    return () => {
      window.removeEventListener("user-updated", sync);
      window.removeEventListener("storage", sync);
    };
  }, []);

    const name = user?.username ?? "user";
    const email = user?.email ?? "";

    const initial = name.charAt(0).toUpperCase();

    return (
    <header className="navbar">
      <div className="navbar-brand">
        <span className="navbar-logo-dot" />
        <span className="navbar-title">WorkBoard</span>
      </div>

        
      <div className="navbar-user">
        <div className="navbar-avatar" aria-hidden>
          {initial}
        </div>
        
        <div className="navbar-user-text">
          <span className="navbar-username">{name}</span>
          {email && (
            <span className="navbar-email">{email}</span>
          )}
        </div>
        
      </div>
    </header>
  );
}