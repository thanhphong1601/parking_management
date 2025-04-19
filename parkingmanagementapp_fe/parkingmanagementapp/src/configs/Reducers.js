import cookie from "react-cookies"

const MyUserReducer = (current, action) => {
    switch (action.type) {
        case "login":
            return action.payload;
        case "logout":
            localStorage.removeItem("token");
            return null;
    }
    return current;
}

export default MyUserReducer;