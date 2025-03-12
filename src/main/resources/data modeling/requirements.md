# Data Modeling Requirements

## Users
### General Users
- Can create an account with the following attributes:
  - **Email** (unique, required)
  - **Phone** (unique, required)
  - **Password** (required)
  - **Username** (unique, required)
  - **Profile Picture** (optional)
  - Receive a confirmation link via email
- Can update their account details:
  - Email (unique)
  - Password
  - Username (unique)
  - Profile Picture
  - Preferred Language
  - Enable Multifactor Authentication with phone SMS
- Can delete their account

## Blog Articles
- All users can:
  - Search blog articles by **title keyword**
  - Sort blog articles by **date**

## User Roles
### General Users
- Have all the functionalities mentioned above

### Admins (Extended User Role)
- Have all functionalities of General Users
- Can create blog articles with the following attributes:
  - **Title**
  - **Date of Creation** (auto-generated)
  - **Cover Image** (optional)
  - **Description**
- Can modify or delete any blog article
- Can delete user accounts