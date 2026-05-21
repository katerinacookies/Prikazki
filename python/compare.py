import os
from pathlib import Path

def scan_directory_base_names(dir_path):
    """
    Recursively scans a directory and returns a dictionary mapping
    the relative path (without extension) to its original filename.
    """
    file_data = {}
    base_path = Path(dir_path).resolve()
    
    if not base_path.exists():
        print(f"Error: Directory '{dir_path}' does not exist.")
        return None

    for root, _, files in os.walk(base_path):
        for file in files:
            full_path = Path(root) / file
            rel_path = full_path.relative_to(base_path)
            
            # Strip the extension (e.g., 'sub/file.json' -> 'sub/file')
            rel_path_no_ext = rel_path.with_suffix('')
            
            # Map the clean path to the actual original filename for reporting
            file_data[str(rel_path_no_ext)] = file
                
    return file_data

def find_missing_files(dir1, dir2):
    print(f"Scanning '{dir1}'...")
    data1 = scan_directory_base_names(dir1)
    
    print(f"Scanning '{dir2}'...")
    data2 = scan_directory_base_names(dir2)
    
    if data1 is None or data2 is None:
        return

    files1 = set(data1.keys())
    files2 = set(data2.keys())

    # Find unique files in each directory (ignoring extensions)
    only_in_1 = files1 - files2
    only_in_2 = files2 - files1

    # --- Print Results ---
    print("\n" + "="*50)
    print(" MISSING FILES REPORT (EXTENSIONS IGNORED)")
    print("="*50)

    if not only_in_1 and not only_in_2:
        print("✨ Perfect match! Both directories contain the same file structures.")
        return

    if only_in_1:
        print(f"\n📂 Found in Dir A ('{Path(dir1).name}') but MISSING from Dir B:")
        for f in sorted(only_in_1):
            print(f"  - {f} (Original name: {data1[f]})")

    if only_in_2:
        print(f"\n📂 Found in Dir B ('{Path(dir2).name}') but MISSING from Dir A:")
        for f in sorted(only_in_2):
            print(f"  - {f} (Original name: {data2[f]})")

if __name__ == "__main__":
    dir_a = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/_questions"
    dir_b = "D:/Projects/prikazki/Prikazki/app/src/main/assets/robot/questions"
    
    # dir_a = 

    find_missing_files(dir_a, dir_b)