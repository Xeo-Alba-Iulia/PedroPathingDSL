# PedroPathingDSL

## 🚀 Overview
PedroPathingDSL provides a lightweight Domain-Specific Language (DSL) for building PathChains in PedroPathing for Kotlin. This project aims to replace the existing PathBuilder for Kotlin users, offering a more intuitive and flexible way to define and manage paths. Whether you're a Kotlin developer looking to simplify path planning or a robotics enthusiast, PedroPathingDSL is designed to streamline your workflow.

## ✨ Features
- 🌟 **Lightweight DSL**: A concise and expressive way to define paths.
- 🔄 **Flexible Paths**: Support for various path types, including Bezier curves and parametric paths.
- 🕒 **Temporal and Parametric Callbacks**: Easily add callbacks based on time and parametric values.
- 📈 **Custom Curve Factories**: Define custom curves to fit your specific needs.
- 🔒 **Path Constraints**: Apply constraints to your paths for more precise control.

## 🛠️ Tech Stack
- **Programming Language**: Kotlin
- **Build Tool**: Gradle
- **Dependencies**: Kotlin, JUnit5, Maven Publish

## 📦 Installation

### Prerequisites
- Java Development Kit (JDK) 8 or later
- Gradle 7.0 or later
- Kotlin 2.0.0 or later

### Quick Start
```bash
# Clone the repository
git clone https://github.com/Xeo-Alba-Iulia/PedroPathingDSL.git

# Navigate to the project directory
cd PedroPathingDSL

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Alternative Installation Methods
- **Package Managers**: Use Gradle to manage dependencies.
- **Docker**: Docker setup is not applicable for this project.
- **Development Setup**: Follow the instructions in the [Development Setup](#development-setup) section.

## 🎯 Usage

### Basic Usage
```kotlin
import com.pedropathing.paths.*

fun main() {
    val pathChain = pathChain(null) {
        path {
            +Pose(0.0, 0.0)
            +Pose(1.0, 1.0)
            +Pose(2.0, 0.0)
        }
        pathFacingPoint(Pose(1.0, 1.0)) {
            +Pose(0.0, 0.0)
            +Pose(2.0, 0.0)
        }
        build()
    }

    // Access the first path
    val firstPath = pathChain.firstPath()
    println(firstPath)
}
```

### Advanced Usage
- **Custom Curve Factories**: Define custom curves to fit your specific needs.
- **Path Constraints**: Apply constraints to your paths for more precise control.
- **Callbacks**: Add callbacks based on time and parametric values.

## 📁 Project Structure
```
PedroPathingDSL/
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew.bat
├── .idea/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/
│   │   │       └── pedropathing/
│   │   │           └── paths/
│   │   │               ├── CallbackBuilder.kt
│   │   │               ├── KotlinPath.kt
│   │   │               ├── KotlinPathBuilder.kt
│   │   │               └── PathMarker.kt
│   └── test/
│       ├── kotlin/
│       │   └── com/
│       │       └── pedropathing/
│       │           └── paths/
│       │               ├── KotlinPathBuilderTest.kt
│       │               └── KotlinPathTest.kt
└── README.md
```

## 🔧 Configuration
- **Environment Variables**: None required.
- **Configuration Files**: None required.
- **Customization Options**: Customize paths using the DSL.

## 🤝 Contributing
We welcome contributions! Here's how you can get started:

### Development Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Xeo-Alba-Iulia/PedroPathingDSL.git
   cd PedroPathingDSL
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run tests:
   ```bash
   ./gradlew test
   ```

### Code Style Guidelines
- Follow Kotlin coding conventions.
- Use meaningful variable and function names.
- Keep your code modular and well-documented.

### Pull Request Process
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them.
4. Push your branch to your fork.
5. Open a pull request.

## 📝 License
This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.

## 👥 Authors & Contributors
- **Maintainers**: Team Xeo

## 🐛 Issues & Support
- **Report Issues**: Open an issue on the [GitHub Issues page](https://github.com/Xeo-Alba-Iulia/PedroPathingDSL/issues).
- **Get Help**: Join the [PedroPathingDSL community](https://github.com/Xeo-Alba-Iulia/PedroPathingDSL/discussions).

## 🗺️ Roadmap
- **Planned Features**:
  - Add support for more path types.
  - Improve performance and efficiency.
  - Enhance documentation and examples.


- **Future Improvements**:
  - Integrate with more robotics frameworks.
  - Add support for more complex path constraints.

---

**Badges:**
[![Build Status](https://travis-ci.org/Xeo-Alba-Iulia/PedroPathingDSL.svg?branch=main)](https://travis-ci.org/Xeo-Alba-Iulia/PedroPathingDSL)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Version](https://img.shields.io/badge/Version-0.2.2-blue.svg)](https://github.com/Xeo-Alba-Iulia/PedroPathingDSL/releases)
