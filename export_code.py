import os

# Các đuôi file muốn lấy
EXTENSIONS = {'.java', '.xml'}
# Các thư mục muốn bỏ qua
IGNORE_DIRS = {'build', '.gradle', '.idea', 'gradle', '.git', 'androidTest', 'test'}

def export_project():
    output_file = "full_project_context.txt"
    
    with open(output_file, 'w', encoding='utf-8') as outfile:
        # Lấy đường dẫn thư mục hiện tại
        current_dir = os.getcwd()
        
        for root, dirs, files in os.walk(current_dir):
            # Lọc bỏ các thư mục không cần thiết
            dirs[:] = [d for d in dirs if d not in IGNORE_DIRS]
            
            for file in files:
                if any(file.endswith(ext) for ext in EXTENSIONS):
                    file_path = os.path.join(root, file)
                    # Đường dẫn tương đối để dễ đọc
                    rel_path = os.path.relpath(file_path, current_dir)
                    
                    try:
                        with open(file_path, 'r', encoding='utf-8') as infile:
                            content = infile.read()
                            
                            # Ghi tên file làm tiêu đề
                            outfile.write(f"\n{'='*50}\n")
                            outfile.write(f"FILE: {rel_path}\n")
                            outfile.write(f"{'='*50}\n")
                            outfile.write(content + "\n")
                            print(f"Đã đọc: {rel_path}")
                    except Exception as e:
                        print(f"Lỗi đọc file {rel_path}: {e}")

    print(f"\n✅ Xong! Toàn bộ code đã được lưu vào: {output_file}")

if __name__ == "__main__":
    export_project()