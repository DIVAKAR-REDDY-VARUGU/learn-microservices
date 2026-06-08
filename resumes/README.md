# Resumes

Tailored, per-application resumes — **one folder per company, one file per role**.

## Structure
```
resumes/
  build_resume.py                 # reusable generator (JSON -> .docx + .pdf)
  <Company>/
    <Role>_<JobNumber>.json       # source of truth (edit this)
    <Role>_<JobNumber>.docx       # generated
    <Role>_<JobNumber>.pdf        # generated
```

## Add a new application
1. `mkdir resumes/<Company>`
2. Copy an existing role JSON to `resumes/<Company>/<Role>_<JobNumber>.json` and edit the content.
3. From the `resumes/` folder run:
   ```
   python build_resume.py <Company>/<Role>_<JobNumber>.json
   ```
   This writes the matching `.docx` and `.pdf` right next to the JSON.

## Requirements
```
pip install python-docx reportlab
```
PDF is rendered with **reportlab**, DOCX with **python-docx**. (Word COM automation is not used — it hangs on this machine.)

## JSON schema
```
name            string
contact         [string, ...]            # email, phone, location, github
summary         string
skills          [{category, items}, ...]
experience      [{company, title, dates, location, bullets:[...]}, ...]
projects        [{name, description}, ...]
education       [{institution, detail}, ...]
certifications  [string, ...]
```
