const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const mysql = require('mysql');

const { v4: uuidv4 } = require('uuid');

const BASE_URL= 'http://127.0.0.1:3000';


const app = express();
const port = 3000;

const bodyParser = require('body-parser');
const cors = require('cors');

app.use(cors());
app.use(bodyParser.json());

const connection = mysql.createConnection({
    host: '127.0.0.1',
    port: '3306',
    user: 'admin',
    password: 'admin',
    database: 'studentmarketdb'
});

connection.connect((err) => {
    if (err) {
      console.error('Error connecting to MySQL: ' + err.stack);
      console.error('requt: ' + connection.host + connection.port + connection.user + connection.password + connection.database);
      return;


    }
    console.log('Connected to MySQL as id ' + connection.threadId);
  });

  app.post('/auth', async (req, res) => {
    try {
        const { email, password } = req.body;

        // Check if user with given email exists
        const query = 'SELECT user_id, username, email, password FROM users WHERE email = ?';
        connection.query(query, [email], async (error, results, fields) => {
            if (error) {
                console.error(error);
                return res.status(500).json({ success: false, message: 'Internal server error', data: null });
            }

            if (results.length === 0) {
                return res.status(401).json({ success: false, message: 'Invalid email or password', data: null });
            }

            const user = results[0];

            // Check if password matches
            if (password !== user.password) {
                return res.status(401).json({ success: false, message: 'Invalid email or password', data: null });
            }

            // Generate JWT token
            const token = jwt.sign({ userId: user.user_id }, 'my-secret-key', { expiresIn: '1h' });


            const updateQuery = 'UPDATE users SET token = ? WHERE user_id = ?';
            connection.query(updateQuery, [token, user.id], (error, results, fields) => {
                if (error) {
                    console.error(error);
                    return res.status(500).json({ success: false, message: 'Internal server error', data: null });
                }

                // Return user info and token
                return res.json({
                    success: true,
                    message: 'Login successful',
                    data: {
                        id: user.user_id,
                        name: user.	username,
                        email: user.email,
                        token: token,
                    },
                });
            });
        });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal server error', data: null });
    }
});

app.get('/user_info', (req, res) => {
  const userId = req.userId;

  // Get user info from the database
  const query = 'SELECT user_id, username, email, location_id, created_at, updated_at FROM users WHERE user_id = ?';
  connection.query(query, [userId], (error, results, fields) => {
    if (error) {
      console.error(error);
      return res.status(500).json({ success: false, message: 'Internal server error', data: null });
    }

    if (results.length === 0) {
      return res.status(404).json({ success: false, message: 'User not found', data: null });
    }

    // Return user info
    const user = results[0];
    return res.json({
      success: true,
      message: 'User info retrieved successfully',
      data: {
        id: user.user_id,
        username: user.username,
        email: user.email,
        locationId: user.location_id,
        createdAt: user.created_at,
        updatedAt: user.updated_at
      }
    });
  });
});

app.post('/sign-up', async (req, res) => {
  try {
    const { username, email, password } = req.body;

    // Check if user with given email already exists
    const query = 'SELECT user_id FROM users WHERE email = ?';
    connection.query(query, [email], async (error, results, fields) => {
      if (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal server error', data: null });
      }

      if (results.length > 0) {
        return res.status(400).json({ success: false, message: 'Email already exists', data: null });
      }

      //generate a username from the mail
      const username = email.split('@')[0].trim();

      // Generate a unique INT for the user ID
     
      function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
      }
      

      const user_id = randomInt(0, 2147483647);
      // Insert new user into the database

      //const insertQuery = 'INSERT INTO users (username, email, password, creat_at) VALUES (?, ?, ?, NOW())';
      //connection.query(insertQuery, [username, email, password], (error, results, fields) => {

      const insertQuery = 'INSERT INTO users (user_id, username, email, password, creat_at) VALUES (?, ?, ?, ?, NOW())';
      connection.query(insertQuery, [user_id, username, email, password], (error, results, fields) => {
      
        if (error) {
          console.error(error);
          return res.status(500).json({ success: false, message: 'Internal server error', data: null });
        }

        // Generate JWT token
        const token = jwt.sign({ user_id }, 'my-secret-key', { expiresIn: '1h' });

        // Return user info and token
        return res.json({
          success: true,
          message: 'Sign-up successful',
          data: {
            
            name: username,
            email: email,
            token: token,
          },
        });
      });
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ success: false, message: 'Internal server error', data: null });
  }
});


app.post('/forgot-password', async (req, res) => {
  try {
    const { email } = req.body;

    // Check if user with given email exists
    const query = 'SELECT user_id, username FROM users WHERE email = ?';
    connection.query(query, [email], async (error, results, fields) => {
      if (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal server error', data: null });
      }

      if (results.length === 0) {
        return res.status(400).json({ success: false, message: 'Email does not exist', data: null });
      }

      const { user_id, username } = results[0];

      // Generate a password reset token
      const resetToken = jwt.sign({ user_id }, 'my-secret-key', { expiresIn: '1h' });

      // Send password reset email
      const emailBody = `Hi ${username},\n\nPlease click the following link to reset your password: ${process.env.BASE_URL}/reset-password?token=${resetToken}`;

      
      console.log(`Password reset email sent to ${email}`);

      // Return success response
      return res.json({
        success: true,
        message: 'Password reset email sent',
        data: null,
      });
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ success: false, message: 'Internal server error', data: null });
  }
});

app.post('/reset-password', async (req, res) => {
  try {
    const { email, password, newPassword } = req.body;

    // Check if user with given email exists
    const query = 'SELECT user_id, password FROM users WHERE email = ?';
    connection.query(query, [email], async (error, results, fields) => {
      if (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal server error', data: null });
      }

      if (results.length === 0) {
        return res.status(404).json({ success: false, message: 'User not found', data: null });
      }

      const user = results[0];

      // Check if password matches
      if (password !== user.password) {
        return res.status(401).json({ success: false, message: 'Invalid password', data: null });
      }

      
      // Update user's password in the database
      const updateQuery = 'UPDATE users SET password = ? WHERE user_id = ?';
      connection.query(updateQuery, [password, user.user_id], (error, results, fields) => {
        if (error) {
          console.error(error);
          return res.status(500).json({ success: false, message: 'Internal server error', data: null });
        }

        return res.json({
          success: true,
          message: 'Password reset successful',
          data: null,
        });
      });
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ success: false, message: 'Internal server error', data: null });
  }
});

const multer = require('multer');
const upload = multer({ dest: 'uploads/' });

const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'c:/wamp64/marketStudent/ressources/upload/img');
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
    cb(null, file.fieldname + '-' + uniqueSuffix + '.' + file.originalname.split('.').pop());
  }
});

app.post('/products', upload.array('images'), (req, res) => {
  try {
    const { name, price, description, userId, location } = req.body;

    let imageUrls = [];
    if (req.files && req.files.length > 0) {
      imageUrls = req.files.map((file) => {
        return `${BASE_URL}/${file.path}`;
      });
    }

    const query = 'INSERT INTO products (name, price, description, user_id, location, image_urls) VALUES (?, ?, ?, ?, ?, ?)';
    connection.query(query, [name, price, description, userId, location, JSON.stringify(imageUrls)], (error, results, fields) => {
      if (error) {
        console.error(error);
        return res.status(500).json({ success: false, message: 'Internal server error', data: null });
      }

      return res.json({
        success: true,
        message: 'Product uploaded successfully',
        data: {
          id: results.insertId,
          name: name,
          price: price,
          description: description,
          userId: userId,
          location: location,
          imageUrls: imageUrls
        },
      });
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ success: false, message: 'Internal server error', data: null });
  }
});

app.listen(port, () => {
  console.log(`Server started on http://127.0.0.1:${port}`);
});
