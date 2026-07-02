# smoke test — confirms the venv + toolkit are wired up correctly.
# run it (with the venv active):  python 01-language-core\hello.py
import sys

print(f"Python {sys.version.split()[0]} running from:")
print(sys.executable)

# confirm the data toolkit imported OK
import numpy as np
import pandas as pd

print(f"numpy {np.__version__} | pandas {pd.__version__}")
print("Hello, Python full-stack!  [OK]")
