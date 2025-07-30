# 💰 SpendMaster - Smart Personal Finance Tracker

<div align="center">
  <img src="app/src/main/res/drawable/logo.png" alt="SpendMaster Logo" width="200"/>
  
  [![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
  [![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
  [![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)](https://developer.android.com/jetpack/guide)
  [![Room](https://img.shields.io/badge/Database-Room-green?style=for-the-badge)](https://developer.android.com/training/data-storage/room)
</div>

## 📱 Overview

**SpendMaster** is a comprehensive personal finance management Android application designed to help users track their income, expenses, and budget effectively. Built with modern Android development practices, it provides an intuitive interface for managing personal finances with advanced features like budget tracking, expense categorization, and financial analytics.

## ✨ Key Features

### 🔐 **Security & Privacy**
- **Encrypted Authentication**: Secure user login with encrypted shared preferences
- **Passcode Protection**: Optional passcode lock for enhanced security
- **Data Privacy**: Local data storage with encryption capabilities

### 📊 **Financial Management**
- **Transaction Tracking**: Add, edit, and categorize income/expenses
- **Budget Management**: Set monthly budgets with real-time tracking
- **Smart Categorization**: Automatic categorization of transactions
- **Recurring Transactions**: Support for recurring income/expense tracking

### 📈 **Analytics & Visualization**
- **Interactive Charts**: Beautiful pie charts and bar charts using MPAndroidChart
- **Spending Analytics**: Visual breakdown of spending by category
- **Progress Tracking**: Real-time budget progress indicators
- **Financial Insights**: Monthly spending trends and patterns

### 🔔 **Smart Notifications**
- **Budget Alerts**: Notifications when approaching budget limits
- **Foreground Services**: Reliable notification delivery
- **Customizable Reminders**: Personalized notification settings

### 🌍 **Multi-Currency Support**
- **Currency Conversion**: Support for multiple currencies
- **Dynamic Formatting**: Real-time currency formatting
- **Shared Currency State**: Consistent currency across all screens

### 📤 **Data Management**
- **Export Functionality**: Export financial data to JSON format
- **Backup & Restore**: Secure data backup and restoration
- **Data Portability**: Easy data transfer between devices

## 🛠️ Technology Stack

### **Core Technologies**
- **Language**: Kotlin 100%
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Architecture**: MVVM (Model-View-ViewModel)

### **Android Architecture Components**
- **Navigation Component**: Safe navigation between screens
- **ViewModel**: UI state management and data handling
- **LiveData**: Reactive data streams
- **ViewBinding**: Type-safe view access

### **UI/UX Libraries**
- **Material Design**: Google Material Design components
- **MPAndroidChart**: Interactive charts and graphs
- **ViewPager2**: Smooth onboarding experience
- **ConstraintLayout**: Responsive UI layouts

### **Data Management**
- **SharedPreferences**: Encrypted local data storage
- **Gson**: JSON serialization/deserialization
- **Security Crypto**: Encrypted shared preferences

### **Development Tools**
- **Gradle**: Build automation
- **Kotlin Parcelize**: Efficient data passing
- **Safe Args**: Type-safe navigation

## 📱 Screenshots

<div align="center">
  <img src="app/src/main/res/drawable/ic_onboarding_1.xml" alt="Onboarding" width="200"/>
  <img src="app/src/main/res/drawable/ic_onboarding_2.xml" alt="Smart Budgeting" width="200"/>
  <img src="app/src/main/res/drawable/ic_onboarding_3.xml" alt="Secure & Private" width="200"/>
</div>

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- Kotlin 1.8+
- Gradle 7.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/SpendMaster2.git
   cd SpendMaster2
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if prompted

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

### Build Configuration

The app is configured with the following build settings:
- **Compile SDK**: 35
- **Target SDK**: 35
- **Minimum SDK**: 24
- **Version Code**: 1
- **Version Name**: "1.0"

## 📁 Project Structure

```
app/src/main/java/com/example/spendmasterr/
├── adapter/                 # RecyclerView adapters
├── data/                   # Data layer
│   ├── converter/          # Data converters
│   ├── dao/               # Data Access Objects
│   ├── database/          # Room database components
│   ├── entity/            # Database entities
│   └── repository/        # Repository pattern implementation
├── model/                 # Data models
├── notification/          # Notification management
├── service/              # Background services
├── ui/                   # User interface components
│   ├── analytics/        # Analytics screens
│   ├── budget/          # Budget management
│   ├── dashboard/       # Main dashboard
│   ├── onboarding/      # Onboarding flow
│   ├── passcode/        # Security features
│   ├── settings/        # App settings
│   ├── signinup/        # Authentication
│   └── transactions/    # Transaction management
├── util/                # Utility classes
└── viewmodel/           # Shared ViewModels
```

## 🔧 Key Components

### **Authentication System**
- Encrypted shared preferences for secure credential storage
- Master key encryption using AES256
- Secure login/logout flow

### **Data Management**
- Local data storage using SharedPreferences
- JSON serialization with Gson
- Export/Import functionality for data portability

### **UI Architecture**
- MVVM pattern with ViewModels
- LiveData for reactive UI updates
- Navigation Component for screen navigation
- ViewBinding for type-safe view access

### **Charts & Analytics**
- MPAndroidChart integration for financial visualizations
- Pie charts for category breakdown
- Bar charts for spending trends
- Real-time data updates

## 🎨 Design Features

- **Material Design 3**: Modern, accessible UI components
- **Dark/Light Theme**: Automatic theme switching
- **Smooth Animations**: View transitions and micro-interactions
- **Responsive Layout**: Adapts to different screen sizes
- **Accessibility**: Screen reader support and high contrast

## 🔒 Security Features

- **Encrypted Storage**: Sensitive data encrypted at rest
- **Secure Authentication**: Encrypted credential storage
- **Permission Management**: Minimal required permissions
- **Data Privacy**: Local-only data storage

## 📊 Performance Optimizations

- **ViewBinding**: Efficient view access
- **LiveData**: Reactive data streams
- **Coroutines**: Asynchronous operations
- **Memory Management**: Proper lifecycle handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **MPAndroidChart**: For beautiful chart visualizations
- **Material Design**: For modern UI components
- **Android Jetpack**: For architecture components
- **Kotlin**: For modern Android development

## 📞 Support

If you have any questions or need support, please:
- Open an issue on GitHub
- Check the existing issues for solutions
- Contact the development team

---

<div align="center">
  <p>Made with ❤️ for better financial management</p>
  <p>Built with modern Android development practices</p>
</div>
