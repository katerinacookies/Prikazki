# from pydub import AudioSegment
# import os

# OG_PATH = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/wav"
# NEW_PATH = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/mp3"

# AudioSegment.from_wav("dobritestopani_0.wav").export("test.mp3", format="mp3")

# subfolders = [f.name for f in os.scandir(PATH) if f.is_dir()]
# print(subfolders)
# for subf in subfolders:
#     files = os.listdir(subf)

#     for file in files:
#         if file.endswith(".wav"):
#             filePath = f"{PATH}/{subf}/{file}"
#             play(AudioSegment.from_wav(filePath))

from pydub import AudioSegment
import os

OG_PATH = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot"
NEW_PATH = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/mp3"

# Make sure the new folder exists, just in case
os.makedirs(NEW_PATH, exist_ok=True)

files = os.listdir(OG_PATH)

for file in files:
    if file.endswith(".wav"):
        filePath = f"{OG_PATH}/{file}"
        
        # Make sure the new file gets the .mp3 extension
        new_file_name = file.replace(".wav", ".mp3")
        convertedFilePath = f"{NEW_PATH}/{new_file_name}"
        
        # We use a flag to track if we should convert or not
        should_convert = False
        
        # Check if the converted file already exists in the new path
        if os.path.exists(convertedFilePath):
            # Ask the user what to do and clean up their input (make it lowercase, remove extra spaces)
            answer = input(f"File '{new_file_name}' already exists. Replace it? (yes/no): ").strip().lower()
            
            if answer in ['yes', 'y']:
                print(f"Replacing: {new_file_name}...")
                should_convert = True
            else:
                print(f"Skipped: {new_file_name}")
        else:
            print(f"Converting: {file} -> {new_file_name}...")
            should_convert = True

        # Run the actual conversion if the flag was set to True
        if should_convert:
            AudioSegment.from_wav(filePath).export(convertedFilePath, format="mp3")

print("All done!")

# files = os.listdir(NEW_PATH)

# for file in files:
#     # Find the ones that still have the .wav extension
#     if file.endswith(".wav"):
#         old_file_path = f"{NEW_PATH}/{file}"
        
#         # Swap the extension string
#         new_file_name = file.replace(".wav", ".mp3")
#         new_file_path = f"{NEW_PATH}/{new_file_name}"
        
#         # Tell the operating system to instantly rename it
#         os.rename(old_file_path, new_file_path)
#         print(f"Renamed: {file} -> {new_file_name}")

# print("All files renamed successfully!")