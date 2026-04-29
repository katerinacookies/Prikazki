import os
import shutil
from pydub import AudioSegment

# Define your main source and destination folders
OG_DIR = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/_questions"
NEW_DIR = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/questions"

# os.walk travels through the original directory and all of its subdirectories
for current_folder, subfolders, files in os.walk(OG_DIR):
    
    # Calculate exactly where we are relative to the original base folder
    relative_path = os.path.relpath(current_folder, OG_DIR)
    
    # Map that exact same folder structure to the new destination
    target_folder = os.path.join(NEW_DIR, relative_path)
    
    # Create the cloned folder (and any parent folders) if it doesn't exist yet
    os.makedirs(target_folder, exist_ok=True)
    
    for file in files:
        source_file_path = os.path.join(current_folder, file)
        
        # === HANDLE WAV FILES (CONVERT) ===
        if file.endswith(".wav"):
            new_file_name = file.replace(".wav", ".mp3")
            target_file_path = os.path.join(target_folder, new_file_name)
            
            should_convert = False
            
            if os.path.exists(target_file_path):
                answer = input(f"MP3 '{new_file_name}' already exists in /{relative_path}. Replace? (y/n): ").strip().lower()
                if answer in ['yes', 'y']:
                    print(f"Replacing: {new_file_name}...")
                    should_convert = True
                else:
                    print(f"Skipped: {new_file_name}")
            else:
                print(f"Converting: {file} -> {new_file_name}...")
                should_convert = True

            if should_convert:
                AudioSegment.from_wav(source_file_path).export(target_file_path, format="mp3")
                
        # === HANDLE ALL OTHER FILES (COPY) ===
        else:
            target_file_path = os.path.join(target_folder, file)
            
            should_copy = False
            
            if os.path.exists(target_file_path):
                answer = input(f"File '{file}' already exists in /{relative_path}. Replace? (y/n): ").strip().lower()
                if answer in ['yes', 'y']:
                    print(f"Replacing copy: {file}...")
                    should_copy = True
                else:
                    print(f"Skipped copy: {file}")
            else:
                print(f"Copying: {file}...")
                should_copy = True
                
            if should_copy:
                shutil.copy2(source_file_path, target_file_path)

print("\nClone and conversion completely finished!")