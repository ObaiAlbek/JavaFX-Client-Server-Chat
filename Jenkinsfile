// Das ist die Pipeline-Anleitung für Jenkins

pipeline {
    // 1. Agent: Wo soll das laufen? 'any' = egal, nimm einen freien.
    agent any

    // 2. Tools: Wir sagen Jenkins, dass wir Maven brauchen.
    // HINWEIS: 'M3' ist ein Platzhalter. Du musst in Jenkins unter
    // "Manage Jenkins" -> "Tools" ein Maven-Tool mit diesem Namen einrichten.
    // Alternativ, wenn Maven im Jenkins-Agent installiert ist, kannst du den 'tools' Block weglassen.
    tools {
        maven 'M3' 
    }

    // 3. Stages: Die Schritte der Pipeline
    stages {

        stage('1. Checkout') {
            // Holt den Code von GitHub.
            // 'scm' nutzt die Git-Repo-URL, die wir im Jenkins-Job einrichten.
            steps {
                checkout scm
            }
        }

        stage('2. Build & Test') {
            // Führt Maven aus: 'clean' löscht alte builds, 'package' kompiliert UND testet.
            steps {
                // Bei Linux/macOS: sh 'mvn clean package'
                // Bei Windows: bat 'mvn clean package'
                // Wir nutzen 'sh' da es im Docker-Container (Linux) laufen wird
                sh 'mvn clean package'
            }
        }
    }

    // 4. Post-Actions: Was soll nach den Schritten passieren?
    post {
        // 'always' = wird immer ausgeführt, egal ob Erfolg oder Fehlschlag
        always {
            
            // Sammle die JUnit-Testergebnisse, damit Jenkins sie anzeigen kann
            junit 'target/surefire-reports/*.xml'
            
            // Archiviere die gebaute .jar-Datei, damit man sie herunterladen kann
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            
            echo 'Pipeline abgeschlossen.'
        }
        
        success {
            echo 'Build war erfolgreich!'
            // Hier könnte man eine Slack-Nachricht senden
        }
        
        failure {
            echo 'Build ist fehlgeschlagen!'
            // Hier könnte man eine E-Mail senden
        }
    }
}