import os
import shutil
from pathlib import Path

def merge_directories_force(src_dir, dest_dir, dry_run=False):
    """
    COPIES all files from src_dir into dest_dir, preserving subfolders.
    If a file with the exact same name exists, it will overwrite it.
    """

    ctr = 0

    base_src = Path(src_dir).resolve()
    base_dest = Path(dest_dir).resolve()

    if not base_src.exists():
        print(f"Error: Source directory '{src_dir}' does not exist.")
        return

    print(f"Copying files from: {base_src}")
    print(f"Target destination: {base_dest}")
    if dry_run:
        print("🧪 RUNNING IN DRY-RUN MODE (No files will actually be copied)\n")
    print("="*60)

    copied_count = 0

    for root, _, files in os.walk(base_src):
        for file in files:
            src_file_path = Path(root) / file
            
            # Get the relative path to maintain subfolder structure
            rel_path = src_file_path.relative_to(base_src)
            rel_dir = rel_path.parent
            
            # Determine target directory and target file path
            target_dir = base_dest / rel_dir
            target_file_path = base_dest / rel_path

            # --- Move/Copy Operation ---
            if not dry_run:
                # Create the identical subfolder in Dir B if it doesn't exist yet
                target_dir.mkdir(parents=True, exist_ok=True)
                # Using copy2 instead of move so your source folder remains intact
                shutil.copy2(str(src_file_path), str(target_file_path))

                ctr += 1

            print(f"🚀 {ctr}: Copied: {rel_path}")
            copied_count += 1

    print("="*60)
    print(f"Process Complete! Total Files Processed: {copied_count}")


if __name__ == "__main__":
    dir_a = "C:/Users/User/Desktop/audio backup/questions"
    dir_b = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/questions"
    
    # Change dry_run=False when you are ready to copy
    merge_directories_force(dir_a, dir_b, dry_run=False)