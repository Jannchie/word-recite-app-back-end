ssh -i ~/.ssh/word-deploy -o StrictHostKeyChecking=no  %WORD_SERVER_USERNAME%@%WORD_SERVER_IP% "bash ./word-backend/deploy.sh"
