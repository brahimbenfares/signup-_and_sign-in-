document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
  
    var loginIdentifier = document.getElementById('loginIdentifier').value;
    var password = document.getElementById('password').value;
  
    if (!loginIdentifier || !password) {
      console.error('Login identifier and password are required');
      return;
    }
  
    var data = {
      loginIdentifier: loginIdentifier,
      password: password
    };
  
    console.log('Sending login request:', data);
  
    fetch('http://localhost:8085/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(data)
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => { throw new Error(text) });
      }
      return response.text(); // Return the token as text
    })
    .then(token => {
      console.log('Received token:', token);
      // Store the token and user information
      localStorage.setItem('jwtToken', token);
      localStorage.setItem('username', loginIdentifier);
      // Redirect to the products page
      window.location.href = "success_sign_in.html";
    })
    .catch(error => {
      console.error('Login error:', error);
      // Display a user-friendly error message to the user
      alert('An error occurred during login. Please try again.');
    });
  });
  
  document.getElementById('togglePassword').addEventListener('click', function (e) {
    var passwordInput = document.getElementById('password');
    var typeAttribute = passwordInput.getAttribute('type');
    if (typeAttribute == 'password') {
      passwordInput.setAttribute('type', 'text');
    } else {
      passwordInput.setAttribute('type', 'password');
    }
  });
  