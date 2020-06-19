fn main() {
    let s = "popampumoi";
    let subs = "moi";
    let n: i32 = search(s, subs);
    println!("sdfw");
    println!("{}", s);
    println!("{}", n);
}

fn search(s: &str, subs: &str) -> i32 {
    let mut n: i32 = -1;
    let mut i: i32 = 0;

    for mut index in 0..9 {
        for mut sindex in 0..1 {
            if s.chars().nth(index) == subs.chars().nth(sindex) {
		        n = i;
		        index = index + 1;
		        i = i + 1;
		        if sindex == 1 {
                    return i;
		        }
            } else {
		        sindex = 2;
                n = -1;
            }
        }
	    i = i + 1;
    }
    return n;
}
.
