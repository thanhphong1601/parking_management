import logo from './logo.svg';
import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import Header from './components/common/header';
import { MyDispatchContext, MyUserContext } from './configs/Contexts';
import { useReducer } from 'react';
import MyUserReducer from './configs/Reducers';
import 'bootstrap/dist/css/bootstrap.min.css';
import Footer from './components/common/footer';
import Index from './components/Home/Index';
import Login from './components/User/Login';
import Register from './components/User/Register';
import CustomerList from './components/customer/CustomerList';
import AddCustomer from './components/customer/AddCustomer';
import TicketList from './components/Ticket/TicketList';
import TicketAdd from './components/Ticket/TicketAdd';
import ManageIn from './components/ParkingManage/ManageIn';
import ManageOut from './components/ParkingManage/ManageOut';
import Stat from './components/Statistics/Stat';
import SuccessPayment from './components/Payment/SuccessPayment';


function App() {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <BrowserRouter>
      <MyUserContext.Provider value={user}>
      <MyDispatchContext.Provider value={dispatch}>
        <Header />
        <Routes>
          <Route path='/' element={<Index />}></Route>
          <Route path='/login' element={<Login/>}></Route>
          <Route path='/register' element={<Register/>}></Route>
          <Route path='/customer/list' element={<CustomerList/>}></Route>
          <Route path='/customer/add' element={<AddCustomer/>}></Route>
          <Route path='/ticket/list' element={<TicketList/>}></Route>
          <Route path='/ticket/add' element={<TicketAdd/>}></Route>
          <Route path='/manage/in' element={<ManageIn/>}></Route>
          <Route path='/manage/out' element={<ManageOut/>}></Route>
          <Route path='/stats' element={<Stat/>}></Route>
          <Route path='/successPayment' element={<SuccessPayment/>}></Route>
        </Routes>
        <Footer/>
        </MyDispatchContext.Provider>
      </MyUserContext.Provider>
      
    </BrowserRouter>
  );
}

export default App;
