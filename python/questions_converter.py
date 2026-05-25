import os
import shutil
from pydub import AudioSegment
from pathlib import Path

# Define your main source and destination folders
SCRIPT_DIR = Path(__file__).resolve().parent

# 2. Go up one folder level to reach the root, then navigate down to 'app'
OG_DIR = SCRIPT_DIR.parent / "app/src/main/assets/robot/_questions"
NEW_DIR = SCRIPT_DIR.parent / "app/src/main/assets/robot/questions"

for current_folder, subfolders, files in os.walk(OG_DIR):
    
    relative_path = os.path.relpath(current_folder, OG_DIR)
    target_folder = os.path.join(NEW_DIR, relative_path)
    os.makedirs(target_folder, exist_ok=True)
    
    for file in files:
        source_file_path = os.path.join(current_folder, file)
        
        # === HANDLE WAV FILES (CONVERT) ===
        if file.endswith(".wav"):
            new_file_name = file.replace(".wav", ".mp3")
            target_file_path = os.path.join(target_folder, new_file_name)
            
            # Skip if target already exists
            if os.path.exists(target_file_path):
                print(f"Skipping (Already exists): {new_file_name}")
            else:
                print(f"Converting: {file} -> {new_file_name}...")
                AudioSegment.from_wav(source_file_path).export(target_file_path, format="mp3")
                
        # === HANDLE ALL OTHER FILES (COPY) ===
        else:
            target_file_path = os.path.join(target_folder, file)
            
            # Skip if target already exists
            if os.path.exists(target_file_path):
                print(f"Skipping (Already exists): {file}")
            else:
                print(f"Copying: {file}...")
                shutil.copy2(source_file_path, target_file_path)

print("\nClone and conversion completely finished!")