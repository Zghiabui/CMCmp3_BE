use musicapp
drop database musicapp
create database musicapp
INSERT INTO songs (
    `id`,
    `title`,
    `artist`,
    `duration`,
    `image_url`, -- 💡 Bao bọc bằng dấu nháy ngược
    `file_path`,
    `listen_count`,
    `like_count`,
    `description`,
    `created_at`,
    `label`
)
VALUES
('1', 'Chúng ta của hiện tại', 'Sơn Tùng M-TP', 200, 'sontung2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/SƠN TÙNG M-TP  CHÚNG TA CỦA HIỆN TẠI  OFFICIAL MUSIC VIDEO - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02', 'pop'),
('2', 'Âm thầm bên em', 'Sơn Tùng M-TP', 200, 'sontung2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Âm Thầm Bên Em  OFFICIAL MUSIC VIDEO  Sơn Tùng M-TP - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02', 'pop'),
('3', 'Trap Queen', 'babysis', 180, 'agirl.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Trap Queen - Adriana Gomez  Eightfold X MKJ Remix (Lyrics + Vietsub) ♫ - Top Tik Tok.mp3', 0, 0, 'nhạc alime', '2025-02-02', 'nightcore'),
('4', 'Sakura', 'single mom', 190, 'agirl.png', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Sakura Anata ni Deaete Yokatta - 5 centimet per second - Lyric Kara HD - Iloveokoloko.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'japanese song'),
('5', 'NHAT', 'Phan Mạnh Quỳnh', 210, 'phanmanhquynh2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('6', 'Yêu 5', 'Jen Hoang', 230, 'boy.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('7', 'Khi phải quyên đi', 'Phan Mạnh Quỳnh', 220, 'phanmanhquynh2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Khi Phải Quên Đi  Phan Mạnh Quỳnh  Official Music Video - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('8', 'Có chàng trai viết lên cây', 'Phan Mạnh Quỳnh', 250, 'phanmanhquynh2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Có Chàng Trai Viết Lên Cây - Phan Mạnh Quỳnh  AUDIO LYRIC OFFICIAL - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('9', 'Lay All Your Love On Me', 'EvanDrago', 260, 'florentino.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/[Lyrics+Vietsub] Abba-Lay All Your Love On Me (Slowed+Reverb) - S P R I N G.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('10', 'Dance Beat', 'DJ Mix', 270, 'denvau.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Lyrics  YÊU 5 - Rhymastic - Jen Hoang.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop');

INSERT INTO `playlist` (
	`id`,
    `name`,
    `description`,
    `image_url`,
    `number_of_songs`,
    `created_at`,
    `listen_count`,
    `like_count`
) VALUES
('l1', 'Maybe you like', 'Popular songs', 'https://i.pinimg.com/736x/82/5a/8e/825a8e195f10950ba0cb0bcf1801ee19.jpg', 110, '2025-02-02', 10, 10),
('l2', 'Recommended for you', 'My personal favorite songs', 'https://i.pinimg.com/736x/82/5a/8e/825a8e195f10950ba0cb0bcf1801ee19.jpg', 5, '2025-02-02', 10, 10),
('l3', 'Top Hits', 'The most popular songs right now', 'https://i.pinimg.com/736x/82/5a/8e/825a8e195f10950ba0cb0bcf1801ee19.jpg', 4, '2025-02-02', 10, 10),
('l4', 'Chill Vibes', 'Relaxing and soothing tracks', 'https://i.pinimg.com/736x/82/5a/8e/825a8e195f10950ba0cb0bcf1801ee19.jpg', 2, '2025-02-02', 10, 10),
('l5', 'Workout Mix', 'High energy songs to keep you moving', 'https://i.pinimg.com/736x/82/5a/8e/825a8e195f10950ba0cb0bcf1801ee19.jpg', 2, '2025-02-02', 10, 10);