var passwordInput = document.getElementById('newPassword');
var passwordStrengthMessage = document.getElementById('passwordStrengthMessage');

passwordInput.addEventListener('input', function() {
  var password = passwordInput.value;
  var passwordStrength = isStrongPassword(password);

  if (passwordStrength.isStrong) {
    passwordStrengthMessage.innerText = 'Strong password';
    passwordStrengthMessage.style.color = 'green';
  } else {
    var missingRequirements = [];

    if (!passwordStrength.hasUppercase) {
      missingRequirements.push('uppercase letter');
    }
    if (!passwordStrength.hasLowercase) {
      missingRequirements.push('lowercase letter');
    }
    if (!passwordStrength.hasNumber) {
      missingRequirements.push('number');
    }
    if (!passwordStrength.hasSymbol) {
      missingRequirements.push('symbol');
    }

    passwordStrengthMessage.innerText = 'Weak password. Missing: ' + missingRequirements.join(', ');
    passwordStrengthMessage.style.color = 'red';
  }
});

document.getElementById('createAccountForm').addEventListener('submit', function(event) {
  event.preventDefault(); // Prevent form submission

  var password = document.getElementById('newPassword').value;
  var confirmPassword = document.getElementById('confirmPassword').value;
  var passwordMatchMessage = document.getElementById('passwordMatchMessage');

  if (password !== confirmPassword) {
    passwordMatchMessage.innerText = 'Passwords do not match';
    passwordMatchMessage.style.color = 'red';
    return;
  } else {
    passwordMatchMessage.innerText = 'Passwords match';
    passwordMatchMessage.style.color = 'green';
  }

  // Proceed with account creation
  var username = document.getElementById('newUsername').value;
  var email = document.getElementById('newEmail').value;
  var phoneNumber = document.getElementById('phoneNumber').value;

  // Create an object to hold the registration data
  var accountData = {
    username: username,
    email: email,
    password: password,
    phoneNumber: phoneNumber
  };

  // Send the account creation request to the server
  createAccount(accountData);
});

function isStrongPassword(password) {
  // Implement your password strength validation logic here
  // Check for at least 8 characters, uppercase, lowercase, and special characters
  var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
  var strength = {
    isStrong: pattern.test(password),
    hasUppercase: /[A-Z]/.test(password),
    hasLowercase: /[a-z]/.test(password),
    hasNumber: /\d/.test(password),
    hasSymbol: /[@$!%*?&]/.test(password)
  };
  return strength;
}

async function createAccount(accountData) {
  try {
    const response = await fetch('http://localhost:8085/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(accountData)
    });

    if (response.ok) {
      // Account created successfully
      alert('Account created successfully');
      window.location.href = 'signin.html'; // Redirect to success.html
    } else {
      // Account creation failed
      const errorMessage = await response.text();
      alert('Failed to create account: ' + errorMessage);
    }
  } catch (error) {
    console.error('Error:', error);
    alert('An error occurred during account creation');
  }
}