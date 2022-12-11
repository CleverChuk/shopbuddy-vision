# shopbuddy-vision
OCR backend for parsing receipts and barcode

# HOW TO RUN
- You need a Google cloud account
- Follow the steps [here](https://cloud.google.com/vision/docs/setup) to enable Cloud Vision API
- If you have the gcloud suite of tools, you can login with `gcloud login` then just run the server it'll use your credential to authenticate
  - You'll get a warning using this approach, however it's quick to get started.
- The recommended way, is to create a service account(Go figure)
- When the service account private key downloads to your machine, point this environment variable to it
  - `GOOGLE_APPLICATION_CREDENTIALS=/path-to-your-sa.json file`
- You're all set!
