from pydub import AudioSegment
from pydub.playback import play
import os

OG_PATH = "../../Prikazki/app/src/main/assets/robot/wav"
NEW_PATH = "../../Prikazki/app/src/main/assets/robot/mp3"

# AudioSegment.from_wav("dobritestopani_0.wav").export("test.mp3", format="mp3")

# subfolders = [f.name for f in os.scandir(PATH) if f.is_dir()]
# print(subfolders)
# for subf in subfolders:
#     files = os.listdir(subf)

#     for file in files:
#         if file.endswith(".wav"):
#             filePath = f"{PATH}/{subf}/{file}"
#             play(AudioSegment.from_wav(filePath))
files = os.listdir(OG_PATH)

for file in files:
    if file.endswith(".wav"):
        filePath = f"{OG_PATH}/{file}"
        convertedFilePath = f"{NEW_PATH}/{file}"
        AudioSegment.from_wav(filePath).export(convertedFilePath, format="mp3")
        # play(AudioSegment.from_wav(filePath))