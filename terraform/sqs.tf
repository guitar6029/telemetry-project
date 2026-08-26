resource "aws_sqs_queue" "device_import" {
    name = "device-import"

    visibility_timeout_seconds = 300

    redrive_policy = jsonencode({
        deadLetterTargetArn = aws_sqs_queue.device_import_dlq.arn
        maxReceiveCount     = 3
    })
}

resource "aws_sqs_queue" "device_import_dlq" {
    name = "device-import-dlq"
}
