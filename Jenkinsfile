pipeline {
  agent any

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  triggers {
    // Nếu chưa cấu hình GitHub Webhook, dùng polling:
    pollSCM('H/2 * * * *') // 2 phút check 1 lần
  }

  tools {
    // Nếu bạn đã cấu hình JDK/Gradle trong Global Tools, có thể khai báo ở đây
    // jdk 'temurin-21'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh './gradlew clean build -x test'
      }
      post {
        success {
          archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
      }
    }

    stage('Test') {
      steps {
        sh './gradlew test'
      }
      post {
        always {
          junit '**/build/test-results/test/*.xml'
        }
      }
    }

    stage('Docker Image (optional)') {
      when { expression { return fileExists('Dockerfile') } }
      steps {
        sh '''
          IMAGE_NAME=weather-forecast
          docker build -t $IMAGE_NAME:ci-$BUILD_NUMBER .
        '''
      }
    }

    stage('Deploy (placeholder)') {
      when { branch 'main' }
      steps {
        echo 'Implement deploy steps here (e.g., docker compose up, k8s apply, etc.)'
      }
    }
  }

  post {
    success { echo "✅ Build #${env.BUILD_NUMBER} OK" }
    failure { echo "❌ Build #${env.BUILD_NUMBER} FAILED" }
  }
}
