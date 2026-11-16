import { NavLink, useNavigate } from "react-router-dom";
import "./Sidebar.css";
import { logout } from "../../api/auth";

export default function Sidebar(){
    const navigate = useNavigate();

function handleLogout(){
    logout();
    navigate("/login");
}

    return(
        <aside className="sidebar">
            <nav className="sidebar-nav">
                <NavLink to="/home" className={({isActive}) => isActive ? "nav-item active": "nav-item"}>
                    Recognitions
                </NavLink>

                <NavLink to="/rankings" className={({isActive}) => isActive ? "nav-item active": "nav-item"}>
                    Rankigs
                </NavLink>

                <NavLink to="/profile" className={({isActive}) => isActive ? "nav-item active": "nav-item"}>
                    Profile
                </NavLink>
            </nav>

            <button type="button" className="sidebar-logout" onClick={handleLogout}> Log out </button>
        </aside>
    );
}