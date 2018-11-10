JQ='/c/tools/jq-win64.exe'
cat <<EOF| $JQ '.' -c
{
  "id": "837d3acd-285e-478a-8d46-817df0a5b4d9",
  "name": "Google<br>",
  "url": "https://www.google.com \t",
  "tests": [
    {
      "id": "ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9",
      "name": "Google",
      "commands": [
        {
          "id": "160c2276-d9b3-4523-bdf3-b914111ca407",
          "comment": "",
          "command": "open",
          "target": "/images",
          "value": ""
        },
        {
          "id": "856ac533-41f0-4091-813d-6f865cf72985",
          "comment": "",
          "command": "open",
          "target": "/",
          "value": ""
        }
      ]
    }
  ],
  "suites": [
    {
      "id": "05e89807-cb33-4ca6-8ca4-10e1cdf127c3",
      "name": "Default Suite",
      "testNames": [
        "ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9"
      ]
    }
  ],
  "urls": [
    "https://www.google.co.in",
    "https://www.google.co.in"
  ]
}
EOF

